package com.skillsync.apigateway.controller;

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
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final WebClient.Builder webClientBuilder;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping("/dashboard")
    public Mono<ResponseEntity<Map<String, Object>>> getDashboard(
            @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Invalid Authorization header"))
            );
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token.substring(7))
                    .getBody();

            String role = claims.get("role", String.class);
            if (!"ROLE_ADMIN".equals(role)) {
                return Mono.just(
                        ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Access denied"))
                );
            }
        } catch (Exception e) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Invalid or expired JWT token"))
            );
        }

        Mono<Object> usersMono = fetchServiceStats("lb://USER-SERVICE/users/admin/stats", token);
        Mono<Object> sessionsMono = fetchServiceStats("lb://SESSION-SERVICE/sessions/admin/stats", token);
        Mono<Object> mentorsMono = fetchServiceStats("lb://MENTOR-SERVICE/mentors/admin/stats", token);

        return Mono.zip(usersMono, sessionsMono, mentorsMono)
                .map(tuple -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("users", tuple.getT1());
                    result.put("sessions", tuple.getT2());
                    result.put("mentors", tuple.getT3());
                    return ResponseEntity.ok(result);
                });
    }

    private Mono<Object> fetchServiceStats(String uri, String token) {
        return webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .cast(Object.class)
                .onErrorResume(error -> Mono.just("FAILED: " + getErrorMessage(error)));
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private String getErrorMessage(Throwable e) {
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getMessage();
    }
}
