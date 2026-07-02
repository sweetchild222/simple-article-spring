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
public class CommentGreatController {

    @GetMapping("/comment/great")
    public ResponseEntity<?> getCommentGreat(@RequestParam Map<String, String> params) {

        final String userId = ObjectCovert.asString(params.get("user_id"));
        final String commentId = ObjectCovert.asString(params.get("comment_id"));
        final String articleId = ObjectCovert.asString(params.get("article_id"));

        if(!QueryParamChecker.validInteger(userId, 0, null, false))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!QueryParamChecker.validInteger(commentId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!QueryParamChecker.validInteger(articleId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strUserId = "g.user_id=" + userId;
        final String strCommentId = commentId != null ? "and g.comment_id=" + commentId : "";
        final String strArticleId = articleId != null ? "and c.article_id=" + articleId : "";

        String sql = "select g.*, c.article_id from comment_great as g ";
        sql += "inner join comment as c on c.id = g.comment_id ";
        sql += "where " + strUserId + " " + strCommentId + " " + strArticleId;

        final List<Map<String, Object>> list = DataBaseClientPool.getClient(UserContext.userID()).selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/comment/great")
    public ResponseEntity<?> postCommentGreat(@RequestBody @NotNull Map<String, Object> payload) {

        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number commentId = ObjectCovert.asNumber(payload.get("comment_id"));
        final Number great = ObjectCovert.asNumber(payload.get("great"));

        if(userId == null || commentId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "insert ignore into comment_great (user_id, comment_id, great) ";
        sql += "select " + userId + ", " + commentId + ", "  + great + " ";
        sql += "where not exists ";
        sql += "(select 1 from comment_great where user_id=" + userId + " and comment_id=" + commentId + ")";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    @PatchMapping("/comment/great/{greatId}")
    public ResponseEntity<?> patchCommentGreat(@PathVariable long greatId, @RequestBody Map<String, Object> payload) {

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Number great = ObjectCovert.asNumber(payload.get("great"));

        if(great == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(great.equals(-1)  || great.equals(1)))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update comment_great set great = '" + great + "'";
        sql += " where id = " + greatId  + " and user_id=" + UserContext.userID();

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


    @DeleteMapping("/comment/great/{commentId}")
    public ResponseEntity<?> deleteCommentGreat(@PathVariable long commentId) {

        String sql = "delete g from comment_great as g ";
        sql += ("where g.id=" + commentId + " and g.user_id=" + UserContext.userID());

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
}
