package net.inkuk.simple_article.util;


import java.security.SecureRandom;

public class PasswordGenerator {

    public static String generate() {

        final String LOWER = "abcdefghijklmnopqrstuvwxyz";
        final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        final String ALL = LOWER + UPPER + DIGITS + SYMBOLS;
        final SecureRandom random = new SecureRandom();

        int length = 16;

        final StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALL.length());
            password.append(ALL.charAt(randomIndex));
        }

        return password.toString();
    }
}