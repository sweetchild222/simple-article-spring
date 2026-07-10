package net.inkuk.simple_article.util;

import java.time.Instant;
import java.util.*;

public class CertifiedEmail {

    private static final long validTimespan = 60 * 10;  //10 min
    private static final HashMap<String, Long> userJoinMap = new HashMap<>();

    public static boolean putUserJoin(String email) {

        long timestamp = Instant.now().toEpochMilli();

        userJoinMap.entrySet().removeIf(entry -> (timestamp - entry.getValue()) > 1000 * validTimespan);

        if(userJoinMap.containsKey(email))
            return false;

        userJoinMap.put(email, timestamp);

        return true;
    }


    public static boolean removeUserJoin(String email){

        long timestamp = Instant.now().toEpochMilli();

        userJoinMap.entrySet().removeIf(entry -> (timestamp - entry.getValue()) > 1000 * validTimespan);

        if(userJoinMap.containsKey(email)) {
            userJoinMap.remove(email);
            return true;
        }

        return false;
    }


    public static void putPasswordReset(String email) {

    }


    public static String popPasswordReset(String email){


        return null;

    }




}
