package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.authorization.SecurityUser;
import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.UserContext;
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

@RestController
public class UserController {

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


    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody Map<String, String> payload) {

        final String username = payload.get("username");
        final String password = payload.get("password");
        final String profile = payload.get("profile");

        if(username == null || password == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "insert into user (username, password) values (";
        sql += "'" + username + "', ";
        sql += "'" + (new BCryptPasswordEncoder()).encode(password) + "')";

        long id = DataBaseClientPool.getClient().postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }



    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable long userId, @RequestBody Map<String, Object> payload) {

        Map<String, Object> params = new java.util.HashMap<>(Map.of());

        final String password = (String)payload.get("password");
        if(password != null)
            params.put("password", "'" + (new BCryptPasswordEncoder()).encode(password) + "'");

        if(payload.containsKey("profile")){
            final String profile = (String)payload.get("profile");
            params.put("profile", (profile != null ? ("'" + profile + "'") : "null"));
        }

        final String role = (String)payload.get("role");
        if(role != null) {
            if (!Arrays.asList(new String[]{"ADMIN", "USER"}).contains(role))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            params.put("role", "'" + role + "'");
        }

        final Boolean is_delete = (Boolean)payload.get("delete");
        if(is_delete != null)
            params.put("delete_at", is_delete ? "current_timestamp()" : "null");

        if(params.size() != payload.size()) {
            System.out.println(params.keySet());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int size = params.size();

        StringBuilder sqlBuilder = new StringBuilder("update user set ");

        for(String key : params.keySet()) {

            String value =  (String)params.get(key);

            size--;

            sqlBuilder.append(key).append("=").append(value).append(size == 0 ? " " : ", ");
        }

        sqlBuilder.append("where id=").append(userId);

        final String sql = sqlBuilder.toString();

        System.out.println(sql);

        int affectCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

        if(affectCount == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(affectCount == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if(affectCount == 1)
            return new ResponseEntity<>(HttpStatus.OK);
        else {
            System.out.println("unexcepted result: " + String.valueOf(affectCount));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
