package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable long userId) {

        final String sql = "select id, username, profile, create_at from user where id = " + String.valueOf(userId);

        final Map<String, Object> map = DataBaseClientPool.getClient().selectRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //@RequestParam(required = false) String query, @RequestParam(required = false
}
