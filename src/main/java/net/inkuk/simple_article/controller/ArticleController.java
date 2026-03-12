package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ArticleController {

    @PostMapping("/article")
    public ResponseEntity<?> postArticle(@RequestBody @NotNull Map<String, Object> payload) {

        final String title = ObjectCovert.asString(payload.get("title"));
        final String content = ObjectCovert.asString(payload.get("content"));
        final Boolean open = ObjectCovert.asBoolean(payload.get("open"));
        final Boolean posted = ObjectCovert.asBoolean(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || categoryId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strOpen = (open ? "1" : "0");
        final String strPosted = (posted ? "1" : "0");
        final String strUserId = String.valueOf(userId);
        final String strCategoryId = String.valueOf(categoryId);

        String sql = "insert into article (title, content, open, posted, thumbnail, category_id, user_id) ";
        sql += "select '" + title + "', '" + content + "', " + strOpen + ", " + strPosted + ", '" + thumbnail + "', " + strCategoryId + ", " + strUserId + " ";
        sql += "where exists " + "(select 1 from category where id=" +  strCategoryId + ") ";
        sql += "and exists (select 1 from user where id=" + strUserId + ")";

        long id = DataBaseClientPool.getClient().postRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}
