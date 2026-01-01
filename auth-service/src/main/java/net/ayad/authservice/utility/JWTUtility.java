package net.ayad.authservice.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtility {

    private final String secret = "rajamaroc2049EnsamCasablancaEstFesEstOujdaMasterBigData&IoT";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String username, Long userId, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(secretKey)
                .compact();

    }

    public String extractUserRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return extractClaims(token).get("userId", Long.class);
    }


    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return true;
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }



}
