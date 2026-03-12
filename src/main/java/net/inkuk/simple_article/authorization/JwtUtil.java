package net.inkuk.simple_article.authorization;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    private SecretKey getSigningKey() {

        final String key = "Asdfva3209ds0fdfssdo3fjklsjfd23240293wfsadadsfaasdklfj8234234";

        final byte[] keyBytes = Decoders.BASE64.decode(key);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }


    private <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        if(claims == null)
            return null;

        return claimsResolver.apply(claims);
    }


    public int extractUserID(String token){

        final Claims claims = extractAllClaims(token);

        if(claims == null)
            return -1;

        return (int)claims.get("id");
    }


    public Claims extractAllClaims(String token) {

        try {

            return Jwts.parser().verifyWith(this.getSigningKey()).build().parseSignedClaims(token).getPayload();

        } catch (JwtException e) {

            return null;
        }
    }


    private @NotNull Boolean isTokenExpired(String token) {

        Date date = extractExpiration(token);

        if(date == null)
            return true;

        return date.before(new Date());
    }


    public String generateToken(Map<String, Object> claims, String subject) {

        return createToken(claims, subject);
    }


    public String generateToken(String subject) {

        return createToken(new HashMap<>(), subject);
    }


    private String createToken(Map<String, Object> claims, String subject) {

        //final int expirationSecond = 60 * 60;
        final int expirationSecond = 60 * 60;

        return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * expirationSecond))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {

        return (!isTokenExpired(token));
    }
}