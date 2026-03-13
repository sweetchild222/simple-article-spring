package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getArticle(@PathVariable long commentId) {

        String sql = "select * from comment where id=" + String.valueOf(commentId);

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRow(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(list.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Map<String, Object> map = list.getFirst();

        Number userId = ObjectCovert.asNumber(map.get("user_id"));
        Number open = ObjectCovert.asNumber(map.get("open"));

        if(userId == null || open == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(open.longValue() == 0 && (userId.longValue() != UserContext.userID()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
