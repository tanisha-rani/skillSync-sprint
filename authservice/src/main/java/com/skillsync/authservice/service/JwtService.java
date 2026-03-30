package com.skillsync.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSignKey() {

        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 🔹 Generate Token
    public String generateToken(String email, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);  // ✅ add role properly

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    // 🔹 Extract Email
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 🔹 Extract Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔹 Validate Token (FINAL FIX)
    public boolean validateToken(String token, String email) {
        try {
            Claims claims = getClaims(token);
            String tokenEmail = claims.getSubject();
            Date expiration = claims.getExpiration();

            return tokenEmail.equals(email) && !expiration.before(new Date());

        } catch (Exception e) {
            System.out.println("VALIDATION ERROR: " + e.getMessage());
            return false;
        }
    }
    public String extractRole(String token){
        Claims claims=getClaims(token);
        return getClaims(token).get("role",String.class);
    }
}