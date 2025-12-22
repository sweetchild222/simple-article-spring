package net.inkuk.simple_article.util;

public class Log {

    public static void info(String message) {

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        String methodName = e.getMethodName();

        System.out.println("[INF] <" + methodName + "> " + message);
    }


    public static void error(String message) {

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        String methodName = e.getMethodName();

        System.out.println("[ERR] <" + methodName + "> " + message);
    }


    public static void debug(String message) {

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        String methodName = e.getMethodName();

        System.out.println("[DBG] <" + methodName + "> " + message);
    }
}
