package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.authorization.SecurityUser;
import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
public class UserController {

    final int minPasswordLength = 8;
    final int maxPasswordLength = 20;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId) {

        final String sql = "select username, profile, create_at from user where id = " + String.valueOf(userId);

        final Map<String, Object> map = DataBaseClientPool.getClient(UserContext.userID()).getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
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

        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,20}$";

        final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        return pattern.matcher(password).matches();
    }


    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody Map<String, String> payload) {

        final String username = payload.get("username");
        final String password = payload.get("password");
        final String profile = payload.get("profile");

        if(username == null || password == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!validPassword(password))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "insert into user (username, password) select ";
        sql += "'" + username + "', ";
        sql += "'" + (new BCryptPasswordEncoder()).encode(password) + "' ";
        sql += "where not exists ";
        sql += "(select 1 from user where username = '" + username + "')";

        long id = DataBaseClientPool.getClient().postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    private Map<String, String> payloadToSqlItems(final Map<String, Object> payload){

        final Map<String, String> items = new java.util.HashMap<>(Map.of());

        final Boolean is_delete = (Boolean)payload.get("delete");
        if(is_delete != null) {
            if(!(is_delete && payload.size() == 1))
                return null;

            items.put("delete_at", "current_timestamp()");
            items.put("username", "null");
            items.put("profile", "null");
            items.put("password", "null");

            return items;
        }

        final String password = (String)payload.get("password");
        if(password != null) {

            if(!validPassword(password))
                return null;

            if(payload.size() != 1)
                return null;

            items.put("password", "'" + (new BCryptPasswordEncoder()).encode(password) + "'");
            return items;
        }

        if(payload.containsKey("profile")) {
            final String profile = (String)payload.get("profile");
            items.put("profile", (profile != null ? ("'" + profile + "'") : "null"));
        }

        final String role = (String)payload.get("role");
        if(role != null) {
            if (!Arrays.asList(new String[]{"ADMIN", "USER"}).contains(role.toUpperCase()))
                return null;

            items.put("role", "'" + role + "'");
        }

        if(items.size() != payload.size())
            return null;

        return items;
    }


    private @NotNull String makeSQL(final Map<String, String> items, final long userId){

        int size = items.size();

        StringBuilder sqlBuilder = new StringBuilder("update user set ");

        for(String key : items.keySet()) {

            String value =  items.get(key);

            size--;

            sqlBuilder.append(key).append("=").append(value).append(size == 0 ? " " : ", ");
        }

        sqlBuilder.append("where id=").append(userId);

        return sqlBuilder.toString();
    }



    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable long userId, @RequestBody Map<String, Object> payload) {

        final Map<String, String> items = this.payloadToSqlItems(payload);

        if(items == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String sql = this.makeSQL(items, userId);

        int affectCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

        if(affectCount == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(affectCount == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if(affectCount == 1)
            return new ResponseEntity<>(HttpStatus.OK);
        else {
            Log.error("Unexcepted affect count: " + String.valueOf(affectCount));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
