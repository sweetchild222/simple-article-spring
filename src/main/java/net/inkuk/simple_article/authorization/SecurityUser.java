package net.inkuk.simple_article.authorization;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SecurityUser implements UserDetails {

    private final long id;
    private final String username;
    private final String password;
    private final boolean verified;
    private final List<GrantedAuthority> authorityList;

    public SecurityUser(long id, String username, String password, boolean verified, List<GrantedAuthority> authorityList) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.verified = verified;
        this.authorityList = authorityList;
    }


    public static SecurityUser createInvalidUser(){

        return new SecurityUser(-1, null, null, false, null);
    }


    public long getID(){

        return this.id;
    }

    @Override
    public @NotNull String getUsername() {

        return this.username;
    }


    public boolean isInvalid(){

        return (this.id == -1);
    }

    @Override
    public String getPassword() {

        return this.password;
    }

    @Override
    public @NotNull Collection<GrantedAuthority> getAuthorities() {

        return this.authorityList;
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

        return this.verified;
    }
}