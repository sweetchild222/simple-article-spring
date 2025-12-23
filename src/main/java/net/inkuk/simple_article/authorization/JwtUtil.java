package net.inkuk.simple_article.authorization;

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


    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public int extractUserID(String token){

        final Claims claims = extractAllClaims(token);

        return (int)claims.get("id");
    }


    public Claims extractAllClaims(String token) {

        return Jwts.parser().verifyWith(this.getSigningKey()).build().parseSignedClaims(token).getPayload();
    }


    private @NotNull Boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }


    public String generateToken(Map<String, Object> claims, String subject) {

        return createToken(claims, subject);
    }


    public String generateToken(String subject) {

        return createToken(new HashMap<>(), subject);
    }


    private String createToken(Map<String, Object> claims, String subject) {

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