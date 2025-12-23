package net.inkuk.simple_article.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private static LogFile infoLogFile = new LogFile("info");
    private static LogFile errorLogFile = new LogFile("error");

    public static void info(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();

        final String log = "<" + methodName + "> " + message;

        System.out.println("[INF] " + log);

        String time = currentTimeString();

        infoLogFile.write("[" + time + "] " + log);
    }


    private static @NotNull String currentTimeString(){

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        return now.format(formatter);
    }


    public static void error(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();

        final String log = "<" + methodName + "> " + message;

        System.out.println("[ERR] " + log);

        String time = currentTimeString();

        errorLogFile.write("[" + time + "] " + log);
    }


    public static void debug(String message) {

        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        final StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();

        final String log = "[DBG] <" + methodName + "> " + message;

        System.out.println(log);
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
