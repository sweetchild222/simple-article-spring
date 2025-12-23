package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.authorization.JwtUtil;
import net.inkuk.simple_article.authorization.SecurityUser;
import net.inkuk.simple_article.authorization.UserDetailsServiceImpl;
import net.inkuk.simple_article.util.Log;
import org.jetbrains.annotations.NotNull;
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
    public ResponseEntity<?> postAuthenticate(@RequestBody @NotNull Map<String, String> requestBody){

        final String username = requestBody.get("username");
        final String password = requestBody.get("password");

        if(username == null || password == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(token);

        } catch (BadCredentialsException e) {

            Log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        final SecurityUser securityUser = (SecurityUser)userDetailsServiceImpl.loadUserByUsername(username);

        if(securityUser.isInvalid())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        final String jwt = jwtUtil.generateToken(securityUser.getUsername());

        final Map<String, Object> body = Map.of("jwt", jwt, "user_id", securityUser.getID());

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}