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

public class BlogController {


    @GetMapping("/blog/{blogId}")
    public ResponseEntity<?> getBlog(@PathVariable long blogId) {

        String sql = "select * from blog where id=" + blogId;

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if (map == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
