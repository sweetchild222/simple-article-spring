package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @GetMapping("/article/{articleId}/comment")
    public ResponseEntity<?> getArticleComment(@PathVariable long articleId) {

        String sql = "select * from comment where article_id=" + articleId;
        sql += " order by create_at asc";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long commentId) {

        String sql = "delete from comment where id = " + commentId;
        sql += " and user_id = " + UserContext.userID();

        int affectCount = DataBaseClientPool.getClient(UserContext.userID()).deleteRow(sql);

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


    @PostMapping("/comment")
    public ResponseEntity<?> postComment(@RequestBody @NotNull Map<String, Object> payload) {

        final String comment = ObjectCovert.asString(payload.get("comment"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number articleId = ObjectCovert.asNumber(payload.get("article_id"));
        final Number commentId = ObjectCovert.asNumber(payload.get("comment_id"));

        if(comment == null || userId == null || articleId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strUserId = String.valueOf(userId);
        final String strArticleId = String.valueOf(articleId);
        final String strCommentId = commentId != null ? String.valueOf(commentId) : "null";

        String sql = "insert into comment (comment, user_id, article_id, comment_id) ";
        sql += "select '" + comment + "', " + strUserId + ", " + strArticleId + ", " + strCommentId;
        sql += " where exists " + "(select 1 from article where id=" +  strArticleId + ")";
        sql += commentId != null ? " and exists (select 1 from comment where id=" + strCommentId + ")" : "";

        long id = DataBaseClientPool.getClient(UserContext.userID()).postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}
