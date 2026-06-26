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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class AlarmController {

    @GetMapping("/user/{userId}/alarm")
    public ResponseEntity<?> getAlarm(@PathVariable long userId) {

        if(UserContext.userID() != userId)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final String sql = "select * from alarm where to_user_id = " + userId;

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/alarm")
    public ResponseEntity<?> postAlarm(@RequestBody @NotNull Map<String, Object> payload) {

        final Number toUserId = ObjectCovert.asNumber(payload.get("to_user_id"));
        final Number fromUserId = ObjectCovert.asNumber(payload.get("from_user_id"));
        final String type = ObjectCovert.asString(payload.get("type"));
        final Number commentId = ObjectCovert.asNumber(payload.get("comment_id"));

        if(toUserId == null || type == null || fromUserId == null || commentId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(fromUserId.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!Arrays.asList(new String[]{"COMMENT","REPLY","MENTION"}).contains(type.toUpperCase()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String strToUserId = String.valueOf(toUserId);
        final String strFromUserId = String.valueOf(fromUserId);
        final String strCommentId = String.valueOf(commentId);

        String sql = "insert ignore into alarm (from_user_id, to_user_id, type, comment_id) ";
        sql += "values (" + strFromUserId + ", " + strToUserId + ", '" + type + "', " + strCommentId + ")";

        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }


    @DeleteMapping("/alarm/{alarmId}")
    public ResponseEntity<?> deleteAlarm(@PathVariable long alarmId) {

        String sql = "delete from alarm where id = " + alarmId;
        sql += " and to_user_id = " + UserContext.userID();

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

    @PatchMapping("/alarm/{alarmId}")
    public ResponseEntity<?> patchAlarm(@PathVariable long alarmId, @RequestBody Map<String, Object> payload) {

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Number checked = ObjectCovert.asNumber(payload.get("checked"));

        if(checked == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(checked.longValue() == 0 || checked.longValue() == 1))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update alarm set checked = '" + checked + "'";
        sql += " where id = " + alarmId  + " and to_user_id=" + UserContext.userID();

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

}
