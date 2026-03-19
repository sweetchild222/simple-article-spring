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
public class ArticleController {

    @GetMapping("/article")
    public ResponseEntity<?> getArticles(@RequestParam Map<String, String> params) {

        final String offset = ObjectCovert.asString(params.get("offset"));
        final String limit = ObjectCovert.asString(params.get("limit"));
        final String order = ObjectCovert.asString(params.get("order"));

        if (!QueryParamChecker.validInteger(offset, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(limit, 1, 20, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(order, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String sql = makeSql(offset, limit, order);

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    private static @NotNull String makeSql(String offset, String limit, String order) {

        final String strOpen = "open=1";
        final String strPosted = "posted=1";
        final String strOffset = "offset " + (offset != null ? offset : "0");
        final String strLimit = "limit " + (limit != null ? limit : "20");
        final String strOrder = "order by create_at " + (order != null ? (order.equals("0") ? "asc" : "desc") : "asc");

        String sql = "select a.id, a.title, a.thumbnail, a.create_at, a.update_at, a.category_id, c.user_id ";
        sql += "from article as a inner join category as c on a.category_id = c.id where ";
        sql += strOpen + " and " + strPosted + " ";
        sql += strOrder + " " + strLimit + " " + strOffset;

        return sql;
    }


    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getArticle(@PathVariable long articleId) {

        String sql = "select * from article as a inner join category as c on a.category_id = c.id where a.id=" + articleId;
        sql += UserContext.isGuest() ? " and a.open=1 and a.posted=1" : " and c.user_id = " + UserContext.userID();

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

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

        if(title == null || content == null || open == null || posted == null || thumbnail == null || categoryId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strOpen = (open.longValue() == 1 ? "1" : "0");
        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strCategoryId = String.valueOf(categoryId);
        final String strUserId = String.valueOf(UserContext.userID());

        String sql = "insert into article (title, content, open, posted, thumbnail, category_id) ";
        sql += "select '" + title + "', '" + content + "', " + strOpen + ", " + strPosted + ", '" + thumbnail + "', " + strCategoryId;
        sql += " where exists " + "(select 1 from category where id=" +  strCategoryId + " and user_id=" + strUserId + ")";

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
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || open == null || posted == null || thumbnail == null || categoryId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strOpen = (open.longValue() == 1 ? "1" : "0");
        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strUserId = String.valueOf(UserContext.userID());
        final String strCategoryId = String.valueOf(categoryId);

        String sql = "update article set ";
        sql += "title='" + title + "', content='" + content + "', open=" + strOpen;
        sql += ", posted=" + strPosted + ", thumbnail='" + thumbnail +"', category_id=" + strCategoryId;
        sql += " where id=" + articleId;
        sql += " and exists " + "(select 1 from category where id=" +  strCategoryId + " and user_id=" + strUserId + ")";

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

    @GetMapping("/user/{userId}/article")
    public ResponseEntity<?> getArticles(@PathVariable long userId, @RequestParam Map<String, String> params) {

        final String open = ObjectCovert.asString(params.get("open"));
        final String posted = ObjectCovert.asString(params.get("posted"));
        final String categoryId = ObjectCovert.asString(params.get("category_id"));
        final String offset = ObjectCovert.asString(params.get("offset"));
        final String limit = ObjectCovert.asString(params.get("limit"));
        final String order = ObjectCovert.asString(params.get("order"));

        if(!QueryParamChecker.validInteger(open, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!QueryParamChecker.validInteger(posted, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(categoryId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(offset, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(limit, 1, 20, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(order, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId != UserContext.userID()) {

            if(open != null || posted != null || categoryId != null)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final String sql = this.makeSelectSql(String.valueOf(userId), open, posted, categoryId, offset, limit, order);

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    private @NotNull String makeSelectSql(String userId, String open, String posted, String categoryId, String offset, String limit, String order) {

        final String strUserId = "c.user_id=" + userId;
        final String strOpen = open != null ? "a.open=" + (open.equals("1") ? "1" : "0") : "";
        final String strPosted = posted != null ? "a.posted=" + (posted.equals("1") ? "1" : "0") : "";
        final String strCategoryId = categoryId != null ? "a.category_id=" + categoryId : "";
        final String strOffset = "offset " + (offset != null ? offset : "0");
        final String strLimit = "limit " + (limit != null ? limit : "20");
        final String strOrder = "order by create_at " + (order != null ? (order.equals("0") ? "asc" : "desc") : "asc");

        String sql = "select a.id, a.title, a.category_id, a.open, a.posted, a.thumbnail, a.create_at, a.update_at, c.user_id ";
        sql += "from article as a inner join category as c on a.category_id = c.id where ";
        sql += strUserId;
        sql += strOpen.isEmpty() ? "" : (" and " + strOpen);
        sql += strPosted.isEmpty() ? "" : (" and " + strPosted);
        sql += strCategoryId.isEmpty() ? "" : (" and " + strCategoryId);
        sql += " " + strOrder + " " + strLimit + " " + strOffset;

        return sql;
    }
}
