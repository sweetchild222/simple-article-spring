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

import java.util.List;
import java.util.Map;

@RestController
public class BookmarkController {

    @GetMapping("/user/{userId}/bookmark")
    public ResponseEntity<?> getBookmarks(@PathVariable long userId, @RequestParam Map<String, String> params) {

        final String articleId = ObjectCovert.asString(params.get("article_id"));

        if (!QueryParamChecker.validInteger(articleId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "select * from bookmark ";
        sql += "where user_id=" + userId;
        sql += articleId != null ? (" and article_id=" + articleId) : "";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @DeleteMapping("/bookmark/{bookmarkId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable long bookmarkId) {

        String sql = "delete from bookmark where id = " + bookmarkId;
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


    @PostMapping("/bookmark")
    public ResponseEntity<?> postBookmark(@RequestBody @NotNull Map<String, Object> payload) {

        final Number article_id = ObjectCovert.asNumber(payload.get("article_id"));
        final Number user_id = ObjectCovert.asNumber(payload.get("user_id"));

        if(article_id == null || user_id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(user_id.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String sql = "insert ignore into bookmark (article_id, user_id) ";
        sql += "select " + article_id + ", " + user_id + " ";
        sql += "where (select count(*) from article where id=" + article_id + ") ";
        sql += "and (select count(*) from user where id=" + user_id + ")";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}