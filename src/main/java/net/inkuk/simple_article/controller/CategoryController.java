package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import net.inkuk.simple_article.util.QueryParamChecker;
import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.LogoutDsl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    @GetMapping("/user/{userId}/category")
    public ResponseEntity<?> getCategories(@PathVariable long userId) {

        String sql = "select * from category where user_id = " + String.valueOf(userId);
        sql += " order by id asc";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().getRows(sql);

        if (list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<?> deleteCategories(@PathVariable long categoryId) {

        String sql = "delete from category where id = " + String.valueOf(categoryId);
        sql += " and user_id = " + String.valueOf(UserContext.userID());
        sql += " and not exists " + "(select 1 from article where category_id = " + categoryId + ")";

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


    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<?> patchCategory(@PathVariable long categoryId, @RequestBody Map<String, String> payload) {

        //if(userId != UserContext.userID())
            //return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String name = payload.get("name");

        if(name == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update category set name = '" + name + "'";
        sql += " where id = " + String.valueOf(categoryId)  + " and user_id=" + String.valueOf(UserContext.userID());

        Log.debug(sql);

        int matchCount = DataBaseClientPool.getClient(UserContext.userID()).updateRow(sql);

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