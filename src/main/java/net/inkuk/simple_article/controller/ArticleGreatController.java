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
public class ArticleGreatController {


    @DeleteMapping("/article/great/{greatId}")
    public ResponseEntity<?> deleteArticleGreat(@PathVariable long greatId) {

        String sql = "delete g from article_great as g ";
        sql += ("where g.id=" + greatId + " and g.user_id=" + UserContext.userID());

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

    @GetMapping("/article/great")
    public ResponseEntity<?> getArticleGreat(@RequestParam Map<String, String> params) {

        final String userId = ObjectCovert.asString(params.get("user_id"));
        final String articleId = ObjectCovert.asString(params.get("article_id"));

        if(!QueryParamChecker.validInteger(userId, 0, null, false))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!QueryParamChecker.validInteger(articleId, 0, null, false))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "select * from article_great ";
        sql +=  "where user_id=" + userId + " and article_id=" + articleId;

        final List<Map<String, Object>> list = DataBaseClientPool.getClient(UserContext.userID()).selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/article/great")
    public ResponseEntity<?> postGreat(@RequestBody @NotNull Map<String, Object> payload) {

        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number articleId = ObjectCovert.asNumber(payload.get("article_id"));
        final Number great = ObjectCovert.asNumber(payload.get("great"));

        if(userId == null || articleId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "insert ignore into article_great (user_id, article_id, great) ";
        sql += "select " + userId + ", " + articleId + ", "  + great + " ";
        sql += "where not exists ";
        sql += "(select 1 from article_great where user_id=" + userId + " and article_id=" + articleId + ")";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    @PatchMapping("/article/great/{greatId}")
    public ResponseEntity<?> patchArticleGreat(@PathVariable long greatId, @RequestBody Map<String, Object> payload) {

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Number great = ObjectCovert.asNumber(payload.get("great"));

        if(great == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(great.equals(-1)  || great.equals(1)))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update article_great set great = '" + great + "'";
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
}
