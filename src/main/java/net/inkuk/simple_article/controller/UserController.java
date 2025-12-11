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

import java.util.Map;
import java.util.Objects;

@RestController
public class UserController {

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId) {

        final String sql = "select username, profile, create_at from user where id = " + String.valueOf(userId);

        final Map<String, Object> map = DataBaseClientPool.getClient(new UserContext().userID()).getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUsername(@PathVariable String username) {

        final String sql = "select username from user where username = '" + username + "'";

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody Map<String, String> requestBody) {

        final String username = requestBody.get("username");
        final String password = requestBody.get("password");
        final String profile = requestBody.get("profile");

        if(username == null || password == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        final String encryptPassword = encoder.encode(password);

        String sql = "insert into user (username, password) values (";
        sql += "'" + username + "', ";
        sql += "'" + encryptPassword + "')";

        long id = DataBaseClientPool.getClient().postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


}
