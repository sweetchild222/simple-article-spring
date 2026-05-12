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

        if (!QueryParamChecker.validInteger(limit, 1, 100, true))
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

        final String strPosted = "a.posted=1";
        final String strSourceId = "a.source_id is null";
        final String strGroupBy = "group by a.id";
        final String strOrder = "order by create_at " + (order != null ? (order.equals("0") ? "asc" : "desc") : "asc");
        final String strLimit = "limit " + (limit != null ? limit : "100");
        final String strOffset = "offset " + (offset != null ? offset : "0");

        String sql = "select a.id, a.title, a.head, a.thumbnail, a.create_at, a.update_at, a.category_id, b.user_id, c.blog_id, ";
        sql += "count(distinct g.id) as great_count, count(distinct m.id) as comment_count ";
        sql += "from article as a inner join category as c on a.category_id = c.id ";
        sql += "inner join blog as b on c.blog_id = b.id ";
        sql += "left join article_great as g on a.id = g.article_id ";
        sql += "left join comment m on a.id = m.article_id where ";
        sql += strPosted + " and " + strSourceId + " ";
        sql += strGroupBy + " " + strOrder + " " + strLimit + " " + strOffset;

        return sql;
    }


    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getArticle(@PathVariable long articleId) {

        String sql = "select a.*, c.blog_id as blog_id, count(distinct g.id) as great_count, count(distinct m.id) as comment_count from article as a ";
        sql += "inner join category as c on a.category_id = c.id ";
        sql += "left join article_great as g on a.id = g.article_id ";
        sql += "left join comment m on a.id = m.article_id ";
        sql += "where a.id=" + articleId + " ";
        sql += "and (a.posted=1 or (c.blog_id = " + UserContext.blogID() + "))";

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
        final String head = ObjectCovert.asString(payload.get("head"));
        final Number posted = ObjectCovert.asNumber(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));
        final Number sourceId = ObjectCovert.asNumber(payload.get("source_id"));

        if(title == null || content == null || posted == null || thumbnail == null || categoryId == null || head == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(sourceId != null && posted.longValue() == 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(title.length() > 256)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(content.length() > 65535)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(head.length() > 256)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(thumbnail.length() > 512)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strCategoryId = String.valueOf(categoryId);
        final String strBlogId = String.valueOf(UserContext.blogID());
        final String strSourceId = sourceId != null ? String.valueOf(sourceId) : "null";
        final int maxNotPostedCount = 10;

        String sql = "insert ignore into article (title, head, content, posted, thumbnail, category_id, source_id)";
        sql += " select '" + title + "', '" + head + "', '" + content + "', " + strPosted + ", '" + thumbnail + "', " + strCategoryId + ", " + strSourceId;
        sql += " where (exists " + "(select 1 from category where id=" +  strCategoryId + " and blog_id=" + strBlogId + "))";
        sql += " and (select count(a.id) from article as a inner join category as c on c.id = a.category_id where posted=0 and c.blog_id=" + strBlogId + ") < " + maxNotPostedCount;
        sql += sourceId != null ? " and exists " + "(select 1 from article as a inner join category as c on a.category_id = c.id where c.blog_id=" + strBlogId + " and a.id=" + strSourceId + ")" : "";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).postRow(sql);

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
        final String head = ObjectCovert.asString(payload.get("head"));
        final Number posted = ObjectCovert.asNumber(payload.get("posted"));
        final String thumbnail = ObjectCovert.asString(payload.get("thumbnail"));
        final Number categoryId = ObjectCovert.asNumber(payload.get("category_id"));

        if(title == null || content == null || posted == null || thumbnail == null || categoryId == null || head == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(title.length() > 256)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(content.length() > 65535)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(head.length() > 256)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(thumbnail.length() > 512)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strPosted = (posted.longValue() == 1 ? "1" : "0");
        final String strBlogId = String.valueOf(UserContext.blogID());
        final String strCategoryId = String.valueOf(categoryId);

        String sql = "update article set ";
        sql += "title='" + title + "', content='" + content + "', head='" + head + "'";
        sql += ", posted=" + strPosted + ", thumbnail='" + thumbnail +"', category_id=" + strCategoryId;
        sql += " where id=" + articleId;
        sql += " and exists " + "(select 1 from category where id=" +  strCategoryId + " and blog_id=" + strBlogId + ")";

        final int matchCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

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

        String sql = "delete a from article as a inner join category as c on a.category_id = c.id";
        sql += " where a.id=" + articleId;
        sql += (UserContext.isAdmin() ? "" : " and c.blog_id=" + UserContext.blogID());

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

    @GetMapping("/blog/{blogId}/article")
    public ResponseEntity<?> getArticles(@PathVariable long blogId, @RequestParam Map<String, String> params) {

        final String posted = ObjectCovert.asString(params.get("posted"));
        final String categoryId = ObjectCovert.asString(params.get("category_id"));
        final String offset = ObjectCovert.asString(params.get("offset"));
        final String limit = ObjectCovert.asString(params.get("limit"));
        final String order = ObjectCovert.asString(params.get("order"));
        final String sourceId = ObjectCovert.asString(params.get("source_id"));

        if(!QueryParamChecker.validInteger(posted, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(categoryId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(offset, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(limit, 1, 100, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(order, 0, 1, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(sourceId, 0, null, true)) {

            if(!sourceId.equals("none"))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(blogId != UserContext.blogID()) {

            if(posted != null) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        final String sql = this.makeSelectSql(String.valueOf(blogId), posted, categoryId, offset, limit, order, sourceId);

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    private @NotNull String makeSelectSql(String blogId, String posted, String categoryId, String offset, String limit, String order, String sourceId) {

        final String strUserId = "b.id=" + blogId;
        final String strSourceId = sourceId != null ? ("a.source_id" + (sourceId.equals("none")  ? " is null" : "=" + sourceId)) : "";
        final String strPosted = "a.posted=" + (posted != null ? posted : "1");
        final String strCategoryId = categoryId != null ? "a.category_id=" + categoryId : "";
        final String strOffset = "offset " + (offset != null ? offset : "0");
        final String strGroupBy = "group by a.id";
        final String strLimit = "limit " + (limit != null ? limit : "100");
        final String strOrder = "order by create_at " + (order != null ? (order.equals("0") ? "asc" : "desc") : "asc");

        String sql = "select a.id, a.title, a.head, a.showed, a.category_id, a.posted, a.thumbnail, a.create_at, a.update_at, a.source_id, b.user_id, c.blog_id, ";
        sql += "count(distinct g.id) as great_count, count(distinct m.id) as comment_count ";
        sql += "from article as a inner join category as c on a.category_id = c.id ";
        sql += "inner join blog as b on c.blog_id = b.id ";
        sql += "left join article_great as g on a.id = g.article_id ";
        sql += "left join comment as m on a.id = m.article_id ";
        sql += "where " + strUserId;
        sql += strSourceId.isEmpty() ? "" : (" and " + strSourceId);
        sql += " and " + strPosted;
        sql += strCategoryId.isEmpty() ? "" : (" and " + strCategoryId);
        sql += " " + strGroupBy + " " + strOrder + " " + strLimit + " " + strOffset;

        return sql;
    }
}
