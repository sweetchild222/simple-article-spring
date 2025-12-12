package net.inkuk.simple_article.util;

import net.inkuk.simple_article.authorization.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class UserContext {

    public static long userID(){

        SecurityUser securityUser = (SecurityUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return Objects.requireNonNull(securityUser).getID();
    }
}
