package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.QueryParamChecker;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class SubscribeController {

    @GetMapping("/subscribe")
    public ResponseEntity<?> getSubscribe(@RequestParam Map<String, String> params) {

        final String userId = ObjectCovert.asString(params.get("user_id"));
        final String blogId = ObjectCovert.asString(params.get("blog_id"));

        if(userId == null && blogId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(userId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(blogId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "select s.* from subscribe as s ";
        sql += "inner join user as u on u.id = s.user_id ";
        sql += "where u.withdraw_at is null ";
        sql += userId != null ? "and s.user_id=" + userId + " " : "";
        sql += blogId != null ? "and s.blog_id=" + blogId + " " : "";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @DeleteMapping("/subscribe/{subscribeId}")
    public ResponseEntity<?> deleteFollow(@PathVariable long subscribeId) {

        String sql = "delete from subscribe where id = " + subscribeId;
        sql += " and user_id = " + UserContext.userID();

        final int affectCount = DataBaseClientPool.getClient(UserContext.userID()).deleteRow(sql);

        if (affectCount == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if (affectCount == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (affectCount == 1)
            return new ResponseEntity<>(HttpStatus.OK);
        else {
            Log.error("Unexcepted affect count: " + affectCount);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/subscribe")
    public ResponseEntity<?> postSubscribe(@RequestBody @NotNull Map<String, Object> payload) {

        final Number user_id = ObjectCovert.asNumber(payload.get("user_id"));
        final Number blog_id = ObjectCovert.asNumber(payload.get("blog_id"));

        if(user_id == null || blog_id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(user_id.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String sql = "insert ignore into subscribe(user_id, blog_id) ";
        sql += "select " + user_id + ", " + blog_id + " ";
        sql += "where exists ";
        sql += "(select 1 from blog where id=" + blog_id + ")";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}