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
public class CategoryController {

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable long categoryId) {

        String sql = "select c.* from category as c ";
        sql += "where c.id=" + categoryId;

        final Map<String, Object> map = DataBaseClientPool.getClient().selectRow(sql);

        if(map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping("/blog/{blogId}/category")
    public ResponseEntity<?> getCategories(@PathVariable long blogId) {

        String sql = "select c.*, count(case when a.posted = 1 then 1 end) as article_count ";
        sql += "from category as c left outer join article as a on a.category_id = c.id ";
        sql += "where c.blog_id=" + blogId + " ";
        sql += "group by c.id ";
        sql += "order by c.id asc";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if (list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<?> deleteCategories(@PathVariable long categoryId) {

        String sql = "delete from category where id = " + categoryId;
        sql += " and blog_id = " + UserContext.blogID();
        sql += " and not exists " + "(select 1 from article where category_id = " + categoryId + ")";

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


    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<?> patchCategory(@PathVariable long categoryId, @RequestBody Map<String, String> payload) {

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String name = payload.get("name");

        if(name == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(name.length() > 16)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update category set name = '" + name + "'";
        sql += " where id = " + categoryId  + " and blog_id=" + UserContext.blogID();
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


    @PostMapping("/category")
    public ResponseEntity<?> postCategory(@RequestBody @NotNull Map<String, Object> payload) {

        final String name = ObjectCovert.asString(payload.get("name"));
        final Number blogId = ObjectCovert.asNumber(payload.get("blog_id"));

        if(name == null || blogId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(name.length() > 16)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(blogId.longValue() != UserContext.blogID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strBlogId = String.valueOf(blogId);

        final int maxCategory = 10;

        String sql = "insert ignore into category (name, blog_id) ";
        sql += "select '" + name + "', " + strBlogId;
        sql += " where (select count(*) from category where blog_id=" + strBlogId + ") < " + maxCategory;

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}