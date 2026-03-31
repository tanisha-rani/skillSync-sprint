package com.skillsync.apigateway.controller;

import com.skillsync.apigateway.clients.MentorClient;
import com.skillsync.apigateway.clients.SessionClient;
import com.skillsync.apigateway.clients.UserClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final UserClient userClient;
    private final SessionClient sessionClient;
    private final MentorClient mentorClient;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Authorization header"));
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token.substring(7))
                    .getBody();

            String role = claims.get("role", String.class);
            if (!"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired JWT token"));
        }

        Map<String, Object> result = new HashMap<>();

        try {
            result.put("users", userClient.getUserStats(token));
        } catch (Exception e) {
            result.put("users", "FAILED: " + getErrorMessage(e));
        }

        try {
            result.put("sessions", sessionClient.getSessionStats(token));
        } catch (Exception e) {
            result.put("sessions", "FAILED: " + getErrorMessage(e));
        }

        try {
            result.put("mentors", mentorClient.getMentorStats(token));
        } catch (Exception e) {
            result.put("mentors", "FAILED: " + getErrorMessage(e));
        }

        return ResponseEntity.ok(result);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private String getErrorMessage(Exception e) {
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getMessage();
    }
}
