package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.authorization.JwtUtil;
import net.inkuk.simple_article.authorization.SecurityUser;
import net.inkuk.simple_article.authorization.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> requestBody){

        final String username = requestBody.get("username");
        final String password = requestBody.get("password");

        if(username == null || password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(token);

        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final SecurityUser securityUser = (SecurityUser)userDetailsServiceImpl.loadUserByUsername(username);

        if(securityUser.isInvalid())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        final String jwt = jwtUtil.generateToken(securityUser.getUsername());

        return ResponseEntity.ok(Map.of("jwt", jwt, "user_id", securityUser.getID()));
    }
}