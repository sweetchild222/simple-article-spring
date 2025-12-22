package net.inkuk.simple_article.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.inkuk.simple_article.util.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl){

        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String bearer = "Bearer ";

        if(authHeader == null || !authHeader.startsWith(bearer)) {
            chain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(bearer.length());

        if(!jwtUtil.validateToken(jwt)) {
            chain.doFilter(request, response);
            return;
        }

        final String username = jwtUtil.extractUsername(jwt);
        final SecurityContext context = SecurityContextHolder.getContext();

        if(username != null && context.getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            context.setAuthentication(authenticationToken);
        }

        chain.doFilter(request,response);
    }
}
