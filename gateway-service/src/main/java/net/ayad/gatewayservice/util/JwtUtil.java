package net.ayad.gatewayservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final String secret = "rajamaroc2049EnsamCasablancaEstFesEstOujdaMasterBigData&IoT";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return true;
    }

    public Long getUserIdFromToken(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public String extractUserRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
