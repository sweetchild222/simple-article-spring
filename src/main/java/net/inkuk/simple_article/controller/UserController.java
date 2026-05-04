package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.QueryParamChecker;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId) {

        String sql = "select u.id as user_id, u.username, u.image, u.nickname, u.create_at, b.id as blog_id ";
        sql += "from user as u inner join blog as b on u.id = b.user_id ";
        sql += "where u.id = " + userId;

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUsers(@RequestParam Map<String, String> params) {

        String paramId = params.get("id");

        if(paramId == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String [] ids = paramId.split(",");

        if(ids.length > 1000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        StringBuilder sqlBuilder = new StringBuilder("select * from user where ");

        int count = ids.length;

        for(String id: ids) {
            if (!QueryParamChecker.validInteger(id, 0, null, false))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else{
                count--;
                sqlBuilder.append("id=").append(count > 0 ? (id + " or ") : id);
            }
        }

        final String sql = sqlBuilder.toString();

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }



    @GetMapping("/user/exist/{username}")
    public ResponseEntity<?> getUserExist(@PathVariable String username) {

        final String sql = "select count(*) > 0 as exist from user where username = '" + username + "'";

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    private boolean validPassword(String password){

        final String reg = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,20}$";

        final Pattern pattern = Pattern.compile(reg);

        return pattern.matcher(password).matches();
    }


    private boolean validUsername(String username){

        final String reg = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

        final Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);

        return pattern.matcher(username).matches();
    }


    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody @NotNull Map<String, String> payload) {

        final String username = payload.get("username");
        final String password = payload.get("password");

        if(username == null || password == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(validPassword(password) && validUsername(username)))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "insert ignore into user (username, password) select ";
        sql += "'" + username + "', ";
        sql += "'" + (new BCryptPasswordEncoder()).encode(password) + "' ";
        sql += "where not exists ";
        sql += "(select 1 from user where username = '" + username + "')";

        final long id = DataBaseClientPool.getClient().postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    private @Nullable Map<String, String> payloadToSqlItems(final @NotNull Map<String, Object> payload){

        try {

            final Map<String, String> items = new java.util.HashMap<>(Map.of());

            final Boolean is_withdraw = (Boolean) payload.get("withdraw");
            if (is_withdraw != null) {
                if (!(is_withdraw && payload.size() == 1))
                    return null;

                items.put("withdraw_at", "current_timestamp()");
                items.put("username", "null");
                items.put("image", "null");
                items.put("password", "null");

                return items;
            }

            final String password = (String) payload.get("password");
            if (password != null) {

                if (!validPassword(password))
                    return null;

                if (payload.size() != 1)
                    return null;

                items.put("password", "'" + (new BCryptPasswordEncoder()).encode(password) + "'");
                return items;
            }

            if (payload.containsKey("image")) {
                final String image = (String) payload.get("image");
                items.put("image", (image != null ? ("'" + image + "'") : "null"));
            }


            if (payload.containsKey("nickname")) {
                final String nickname = (String) payload.get("nickname");
                items.put("nickname", (nickname != null ? ("'" + nickname + "'") : "null"));
            }

            final String role = (String) payload.get("role");
            if (role != null) {
                if (!Arrays.asList(new String[]{"ADMIN", "USER"}).contains(role.toUpperCase()))
                    return null;

                items.put("role", "'" + role + "'");
            }

            if (items.size() != payload.size())
                return null;

            return items;

        } catch (Exception e) {

            Log.error(e.toString());
            return null;
        }
    }



    @GetMapping("/user/{userId}/password/{password}")
    public ResponseEntity<?> getVerifyEmail(@PathVariable long userId, @PathVariable String password) {

        if(userId != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String sql = "select password from user where id=" + String.valueOf(userId);

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final String passwordSource = map.get("password").toString();

        boolean match = (new BCryptPasswordEncoder()).matches(password, passwordSource);

        return new ResponseEntity<>(Map.of("correct", match), HttpStatus.OK);
    }


    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable long userId, @RequestBody Map<String, Object> payload) {

        if(userId != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Map<String, String> items = this.payloadToSqlItems(payload);

        if(items == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String sql = this.makeUpdateSQL(items, userId);

        final int matchCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

        if(matchCount == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(matchCount == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if(matchCount == 1)
            return new ResponseEntity<>(HttpStatus.OK);
        else {
            Log.error("Unexcepted match count: " + matchCount);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private @NotNull String makeUpdateSQL(final @NotNull Map<String, String> items, final long userId){

        int size = items.size();

        StringBuilder sqlBuilder = new StringBuilder("update user set ");

        for(String key : items.keySet()) {

            String value =  items.get(key);

            size--;

            sqlBuilder.append(key).append("=");
            sqlBuilder.append(value);
            sqlBuilder.append(size == 0 ? " " : ", ");
        }

        sqlBuilder.append("where id=").append(userId);

        return sqlBuilder.toString();
    }
}
