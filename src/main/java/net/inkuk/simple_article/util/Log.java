package net.inkuk.simple_article.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private static LogFile infoLogFile = new LogFile("log/info");
    private static LogFile errorLogFile = new LogFile("log/error");


    private static void infoCore(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();

        final String log = "<" + methodName + "> " + message;

        final String green = "\u001B[32m";
        final String reset = "\u001B[0m";

        System.out.println(green + "[INF]" + " " + log);

        String time = currentTimeString();

        infoLogFile.write("[" + time + "] " + log);
    }


    public static void info(String message) {

        infoCore(message);
    }


    public static void info(long message) {

        infoCore(String.valueOf(message));
    }


    public static void info(int message) {

        infoCore(String.valueOf(message));
    }


    public static void info(boolean message) {

        infoCore(String.valueOf(message));
    }


    private static @NotNull String currentTimeString(){

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        return now.format(formatter);
    }


    private static void errorCore(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();

        final String log = "<" + methodName + "> " + message;

        final String red = "\u001B[31m";
        final String reset = "\u001B[0m";

        System.out.println(red + "[ERR]" + " " + log);

        String time = currentTimeString();

        errorLogFile.write("[" + time + "] " + log);
    }


    public static void error(String message) {

        errorCore(message);
    }


    public static void error(long message) {

        errorCore(String.valueOf(message));
    }


    public static void error(int message) {

        errorCore(String.valueOf(message));
    }


    public static void error(boolean message) {

        errorCore(String.valueOf(message));
    }


    private static void debugCore(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[3];
        final String methodName = e.getMethodName();

        final String cyan = "\u001B[93m";
        final String reset = "\u001B[0m";

        final String log = cyan + "[DBG]" +  " <" + methodName + "> " + message;

        System.out.println(log);
    }


    public static void debug(String message) {

        debugCore(message);
    }


    public static void debug(long message) {

        debugCore(String.valueOf(message));
    }


    public static void debug(int message) {

        debugCore(String.valueOf(message));
    }


    public static void debug(boolean message) {

        debugCore(String.valueOf(message));
    }


    public static void close(){

        if(infoLogFile != null) {
            infoLogFile.close();
            infoLogFile = null;
        }


        if(errorLogFile != null) {
            errorLogFile.close();
            errorLogFile = null;
        }
    }
}
