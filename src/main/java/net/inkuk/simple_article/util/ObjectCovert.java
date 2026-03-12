package net.inkuk.simple_article.util;

public class ObjectCovert {


    public static Double asDouble(Object object){

        if (object instanceof Double)
            return (Double)object;
        else
            return null;
    }


    public static Float asFloat(Object object){

        if (object instanceof Float)
            return (Float)object;
        else
            return null;
    }


    public static Number asNumber(Object object){

        if (object instanceof Number)
            return (Number)object;
        else
            return null;
    }


    public static String asString(Object object){

        if (object instanceof String)
            return (String)object;
        else
            return null;
    }

    public static Boolean asBoolean(Object object){

        if (object instanceof Boolean)
            return (Boolean)object;
        else
            return null;
    }
}
