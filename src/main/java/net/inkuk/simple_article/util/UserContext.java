package net.inkuk.simple_article.util;

import net.inkuk.simple_article.authorization.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class UserContext {

    public static long userID(){

        Object object = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if(object instanceof SecurityUser securityUser)
            return Objects.requireNonNull(securityUser).getID();
        else
            return -1;
    }


    public static boolean isAdmin(){

        Object object = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if(object instanceof SecurityUser securityUser)
            return Objects.requireNonNull(securityUser).isAdmin();
        else
            return false;
    }
}
