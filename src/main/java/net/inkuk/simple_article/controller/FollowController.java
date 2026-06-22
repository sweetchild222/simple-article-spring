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
public class FollowController {

    @GetMapping("/follow")
    public ResponseEntity<?> getFollow(@RequestParam Map<String, String> params) {

        final String userId = ObjectCovert.asString(params.get("user_id"));
        final String followingId = ObjectCovert.asString(params.get("following_id"));
        final String status = ObjectCovert.asString(params.get("status"));

        if(userId == null && followingId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(userId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!QueryParamChecker.validInteger(followingId, 0, null, true))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String[] statusList = {"REQUEST", "ACCEPTED", "REJECTED", null};
        if (!Arrays.asList(statusList).contains(status))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "select f.* from follow as f ";
        sql += "inner join user as u on u.id = f.following_id ";
        sql += "where u.withdraw_at is null ";
        sql += userId != null ? "and f.user_id=" + userId + " " : "";
        sql += followingId != null ? "and f.following_id=" + followingId + " " : "";
        sql += status != null ? "and f.status='" + status + "'" : "";

        final List<Map<String, Object>> list = DataBaseClientPool.getClient().selectRows(sql);

        if(list == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @PatchMapping("/follow/{followId}")
    public ResponseEntity<?> patchFollow(@PathVariable long followId, @RequestBody Map<String, String> payload) {

        if(payload.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String status = payload.get("status");

        final String[] statusList = {"REQUEST", "ACCEPTED", "REJECTED"};
        if(!Arrays.asList(statusList).contains(status))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String sql = "update follow set status = '" + status + "'";
        sql += " where id=" + followId  + " and user_id=" + UserContext.userID();

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


    @DeleteMapping("/follow/{followId}")
    public ResponseEntity<?> deleteFollow(@PathVariable long followId) {

        String sql = "delete from follow where id = " + followId;
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


    @PostMapping("/follow")
    public ResponseEntity<?> postFollow(@RequestBody @NotNull Map<String, Object> payload) {

        final Number user_id = ObjectCovert.asNumber(payload.get("user_id"));
        final Number following_id = ObjectCovert.asNumber(payload.get("following_id"));
        final String status = payload.get("status") != null ? ObjectCovert.asString(payload.get("status"))  : "REQUEST";

        if(user_id == null || following_id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String[] statusList = {"REQUEST", "ACCEPTED", "REJECTED"};
        if(!Arrays.asList(statusList).contains(status))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(user_id.longValue() != UserContext.userID())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String sql = "insert ignore into follow(user_id, following_id, status) ";
        sql += "select " + user_id + ", " + following_id + ", '" + status + "' ";
        sql += "where not exists ";
        sql += "(select 1 from follow where user_id=" + user_id + " and following_id=" + following_id + ")";


        final long id = DataBaseClientPool.getClient(UserContext.userID()).insertRow(sql);

        if(id == -1)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if(id == 0)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}