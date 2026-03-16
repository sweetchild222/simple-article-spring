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
public class ArticleController {

    @GetMapping("/article")
    public ResponseEntity<?> getArticles(@RequestParam Map<String, String> params) {

        final String offset = ObjectCovert.asString(params.get("offset"));
        final String limit = ObjectCovert.asString(params.get("limit"));
        final String order = ObjectCovert.asString(params.get("order"));

        String sql = makeSql(offset, limit, order);

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(list.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    private static @NotNull String makeSql(String offset, String limit, String order) {

        final String strOpen = "open=1";
        final String strPosted = "posted=1";
        final String strOffset = "offset " + (offset != null ? offset : "0");
        final String strLimit = "limit " + (limit != null ? limit : "5");
        final String strOrder = "order by create_at " + (order != null ? (order.equals("0") ? "asc" : "desc") : "asc");

        String sql = "select title, thumbnail, create_at, update_at, user_id from article where ";
        sql += strOpen + " and " + strPosted + " ";
        sql += strOrder + " " + strLimit + " " + strOffset;

        return sql;
    }


    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getArticle(@PathVariable long articleId) {

        String sql = "select * from article where id=" + String.valueOf(articleId);

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Number userId = ObjectCovert.asNumber(map.get("user_id"));
        Number open = ObjectCovert.asNumber(map.get("open"));
        Number posted = ObjectCovert.asNumber(map.get("posted"));

        if(userId == null || open == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(open.longValue() == 0 && (userId.longValue() != UserContext.userID()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if(posted.longValue() == 0 && (userId.longValue() != UserContext.userID()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PostMapping("/article")
    public ResponseEntity<?> postArticle(@RequestBody @NotNull Map<String, Object> payload) {

        final String title = ObjectCovert.asString(payload.get("title"));
        final String content = ObjectCovert.asString(payload.get("content"));
        final Number open = ObjectCovert.asNumber(payload.get("open"));
        final Number posted = ObjectCovert.asNumber(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || userId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strOpen = (open.longValue() == 1 ? "1" : "0");
        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strCategoryId = categoryId == null ? "null" : String.valueOf(categoryId);
        final String strUserId = String.valueOf(userId);

        String sql = "insert into article (title, content, open, posted, thumbnail, category_id, user_id) ";
        sql += "select '" + title + "', '" + content + "', " + strOpen + ", " + strPosted + ", '" + thumbnail + "', " + strCategoryId + ", " + strUserId;
        sql += (categoryId != null ? " where exists " + "(select 1 from category where id=" +  strCategoryId + " and user_id=" + strUserId + ")" : "");

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
        final Number open = ObjectCovert.asNumber(payload.get("open"));
        final Number posted = ObjectCovert.asNumber(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || userId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strOpen = (open.longValue() == 1 ? "1" : "0");
        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strUserId = String.valueOf(userId);
        final String strCategoryId = categoryId == null ? "null" : String.valueOf(categoryId);

        String sql = "update article set ";
        sql += "title='" + title + "', content='" + content + "', open=" + strOpen;
        sql += ", posted=" + strPosted + ", thumbnail='" + thumbnail +"', category_id=" + strCategoryId;
        sql += " where id=" + articleId + " and user_id=" + strUserId;
        sql += (categoryId != null ? " and exists " + "(select 1 from category where id=" +  strCategoryId + " and user_id=" + strUserId + ")" : "");

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
