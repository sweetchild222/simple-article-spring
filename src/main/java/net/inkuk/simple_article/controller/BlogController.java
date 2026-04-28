package net.inkuk.simple_article.controller;
import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.QueryParamChecker;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController

public class BlogController {


    @GetMapping("/blog/{blogId}")
    public ResponseEntity<?> getBlog(@PathVariable long blogId) {

        String sql = "select * from blog where id=" + blogId;

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if (map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if(map.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }



    @PatchMapping("/blog/{blogId}")
    public ResponseEntity<?> patchBlog(@PathVariable long blogId, @RequestBody Map<String, Object> payload) {

        if (blogId != UserContext.blogID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Map<String, String> items = this.payloadToSqlItems(payload);

        if(items == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String sql = this.makeUpdateSQL(items, blogId);

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


    private @Nullable Map<String, String> payloadToSqlItems(final @NotNull Map<String, Object> payload){

        try {

            final Map<String, String> items = new java.util.HashMap<>(Map.of());

            final String title = (String) payload.get("title");

            if (title != null)
                items.put("title", "'" + title + "'");

            final String image = (String) payload.get("image");

            if (image != null)
                items.put("image", "'" + image + "'");

            if (items.size() != payload.size())
                return null;

            return items;

        } catch (Exception e) {

            Log.error(e.toString());
            return null;
        }
    }


    private @NotNull String makeUpdateSQL(final @NotNull Map<String, String> items, final long blogId){

        int size = items.size();

        StringBuilder sqlBuilder = new StringBuilder("update blog set ");

        for(String key : items.keySet()) {

            String value =  items.get(key);

            size--;

            sqlBuilder.append(key).append("=");
            sqlBuilder.append(value);
            sqlBuilder.append(size == 0 ? " " : ", ");
        }

        sqlBuilder.append("where id=").append(blogId);

        return sqlBuilder.toString();
    }
}
