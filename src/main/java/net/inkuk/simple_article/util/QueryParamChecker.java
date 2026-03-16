package net.inkuk.simple_article.util;

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
}
