package net.inkuk.simple_article.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueryParamChecker {

    public static boolean validInteger(String value, Integer start, Integer end, boolean acceptNull) {

        if(value == null)
            return acceptNull;

        try {

            int num = Integer.parseInt(value);

            if(start != null){
                if(num < start)
                    return false;
            }

            if(end != null){
                if(num > end)
                    return false;
            }

            return true;

        } catch (NumberFormatException e) {

            return false;
        }
    }



    public static boolean validIntegerList(String values, Integer start, Integer end, boolean acceptNull, int maxCount) {

        if (values == null)
            return acceptNull;

        String[] valueList = values.split(",");

        if (maxCount != -1 && valueList.length > maxCount)
            return false;

        for (String value : valueList) {

            if (!validInteger(value, start, end, false))
                return false;
        }

        return true;
    }
}
