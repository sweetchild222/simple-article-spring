package net.inkuk.simple_article.authorization;

import lombok.RequiredArgsConstructor;
import net.inkuk.simple_article.database.DataBaseClientPool;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public @NotNull UserDetails loadUserByUsername(@NotNull String username) {

        final String sql = "select id, username, password, verified, role from user where username = '" + username + "'";

        final Map<String, Object> map = DataBaseClientPool.getClient().getRow(sql);

        if(map == null)
            return SecurityUser.createInvalidUser();

        if(map.isEmpty())
            return SecurityUser.createInvalidUser();

        final long id = (long)map.get("id");
        final String name = (String)map.get("username");
        final String password = (String)map.get("password");
        final String authority = (String)map.get("authority");
        final boolean verified = ((int)map.get("verified") != 0);

        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + authority));

        return new SecurityUser(id, name, password, verified, authorities);

    }
}