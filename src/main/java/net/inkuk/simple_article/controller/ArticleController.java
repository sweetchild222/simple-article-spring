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

import java.util.Map;

@RestController
public class ArticleController {


    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getArticle(@PathVariable long articleId) {

        String sql = "select a.*, c.user_id as user_id from article as a ";
        sql += "inner join category as c on a.category_id = c.id ";
        sql += "where a.id=" + String.valueOf(articleId);

        final Map<String, Object> map = DataBaseClientPool.getClient(UserContext.userID()).getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Number userId = ObjectCovert.asNumber(map.get("user_id"));
        Number open = ObjectCovert.asNumber(map.get("open"));

        if(userId == null || open == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(open.longValue() == 0 && (userId.longValue() != UserContext.userID()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PostMapping("/article")
    public ResponseEntity<?> postArticle(@RequestBody @NotNull Map<String, Object> payload) {

        final String title = ObjectCovert.asString(payload.get("title"));
        final String content = ObjectCovert.asString(payload.get("content"));
        final Boolean open = ObjectCovert.asBoolean(payload.get("open"));
        final Boolean posted = ObjectCovert.asBoolean(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || categoryId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strOpen = (open ? "1" : "0");
        final String strPosted = (posted ? "1" : "0");
        final String strCategoryId = String.valueOf(categoryId);

        String sql = "insert into article (title, content, open, posted, thumbnail, category_id) ";
        sql += "select '" + title + "', '" + content + "', " + strOpen + ", " + strPosted + ", '" + thumbnail + "', " + strCategoryId + " ";
        sql += "where exists " + "(select 1 from category where id=" +  strCategoryId + ")";

        long id = DataBaseClientPool.getClient(UserContext.userID()).postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    @PutMapping("/article/{articleId}")
    public ResponseEntity<?> putArticle(@PathVariable long articleId, @RequestBody @NotNull Map<String, Object> payload) {

        final String title = ObjectCovert.asString(payload.get("title"));
        final String content = ObjectCovert.asString(payload.get("content"));
        final Boolean open = ObjectCovert.asBoolean(payload.get("open"));
        final Boolean posted = ObjectCovert.asBoolean(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || userId == null || categoryId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strOpen = (open ? "1" : "0");
        final String strPosted = (posted ? "1" : "0");
        final String strUserId = String.valueOf(userId);
        final String strCategoryId = String.valueOf(categoryId);

        String sql = "update article set ";
        sql += "title='" + title + "', content='" + content + "', open=" + strOpen;
        sql += ", posted=" + strPosted + ", thumbnail='" + thumbnail +"', category_id=" + strCategoryId;
        sql += " where id=" + articleId + " and user_id=" + strUserId;

        int matchCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

        if(matchCount == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(matchCount == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if(matchCount == 1)
            return new ResponseEntity<>(HttpStatus.OK);
        else {
            Log.error("Unexcepted affect count: " + matchCount);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<?> deleteArticle(@PathVariable long articleId) {

        final String sql = "delete from article where id=" + articleId + (UserContext.isAdmin() ? "" : " and user_id=" + UserContext.userID());

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



}
