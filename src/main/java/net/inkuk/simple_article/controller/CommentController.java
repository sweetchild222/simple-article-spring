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
public class CommentController {

    @GetMapping("/article/{articleId}/comment")
    public ResponseEntity<?> getArticleComment(@PathVariable long articleId) {

        final String strArticleId = "c.article_id=" + articleId;
        final String strOrder = "order by like_count desc, c.create_at asc";
        final String strGroup = "group by c.id";

        String sql = "select c.*, count(distinct if(g.great=1, g.id, NULL)) as like_count, count(distinct if(g.great=-1, g.id, NULL)) as dislike_count ";
        sql += "from comment as c left join comment_great as g on c.id = g.comment_id ";
        sql += "where " + strArticleId + " ";
        sql += strGroup + " " + strOrder;

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @GetMapping("/comment")
    public ResponseEntity<?> getComments(@RequestParam Map<String, String> params) {

        String paramId = params.get("id");
        if(paramId == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String [] ids = paramId.split(",");

        if(ids.length > 1000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sqlCore = "select c.*, count(distinct if(g.great=1, g.id, NULL)) as like_count, count(distinct if(g.great=-1, g.id, NULL)) as dislike_count ";
        sqlCore += "from comment as c left join comment_great as g on c.id = g.comment_id ";
        sqlCore += "where ";

        StringBuilder sqlBuilder = new StringBuilder(sqlCore);

        int count = ids.length;

        for(String id: ids) {
            if (!QueryParamChecker.validInteger(id, 0, null, false))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else{
                count--;
                sqlBuilder.append("c.id=").append(count > 0 ? (id + " or ") : id);
            }
        }

        final String sql = sqlBuilder.toString() + " group by c.id";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }




    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long commentId) {

        String sql = "delete from comment where id = " + commentId;
        sql += " and user_id = " + UserContext.userID();

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


    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> putComment(@PathVariable long commentId, @RequestBody @NotNull Map<String, Object> payload) {

        final String comment = ObjectCovert.asString(payload.get("comment"));

        if(comment == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(comment.length() > 1000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update comment set ";
        sql += "comment='" + comment + "' ";
        sql += "where id=" + commentId + " and user_id=" + UserContext.userID();

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


    @PostMapping("/comment")
    public ResponseEntity<?> postComment(@RequestBody @NotNull Map<String, Object> payload) {

        final String comment = ObjectCovert.asString(payload.get("comment"));
        final Number userId = ObjectCovert.asNumber(payload.get("user_id"));
        final Number articleId = ObjectCovert.asNumber(payload.get("article_id"));
        final Number commentId = ObjectCovert.asNumber(payload.get("comment_id"));

        if(comment == null || userId == null || articleId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(comment.length() > 1000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(userId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String strUserId = String.valueOf(userId);
        final String strArticleId = String.valueOf(articleId);
        final String strCommentId = commentId != null ? String.valueOf(commentId) : "null";

        String sql = "insert ignore into comment (comment, user_id, article_id, comment_id) ";
        sql += "select '" + comment + "', " + strUserId + ", " + strArticleId + ", " + strCommentId;
        sql += " where exists " + "(select 1 from article where id=" +  strArticleId + ")";
        sql += commentId != null ? " and exists (select 1 from comment where id=" + strCommentId + ")" : "";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}
