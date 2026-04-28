package net.inkuk.simple_article.authorization;

import net.inkuk.simple_article.util.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SecurityUser implements UserDetails {

    private final long userId;
    private final long blogId;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorityList;

    public SecurityUser(long userId, String username, String password, List<GrantedAuthority> authorityList, long blogId) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorityList = authorityList;
        this.blogId = blogId;
    }


    public static SecurityUser createInvalid(){

        return new SecurityUser(-1, null, null, null, -1);
    }


    public static SecurityUser createEmpty(){

        return new SecurityUser(0, null, null, null, -1);
    }


    public long userID(){

        return this.userId;
    }


    public long blogID(){

        return this.blogId;
    }

    @Override
    public @NotNull String getUsername() {

        return this.username;
    }


    public boolean isInvalid(){

        return (this.userId == -1);
    }


    public boolean isEmpty(){

        return (this.userId == 0);
    }


    @Override
    public String getPassword() {

        return this.password;
    }

    @Override
    public @NotNull Collection<GrantedAuthority> getAuthorities() {

        return this.authorityList;
    }


    public boolean isAdmin(){

        String [] authorities = getAuthoritiesString();

        for (String authoritie : authorities) {

            if (authoritie.equals("ADMIN"))
                return true;
        }

        return false;
    }


    public String [] getAuthoritiesString(){

        ArrayList<String> arrayList = new ArrayList<>();

        for(GrantedAuthority a : this.getAuthorities())
            arrayList.add(a.toString());

        return arrayList.toArray(String[]::new);
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}