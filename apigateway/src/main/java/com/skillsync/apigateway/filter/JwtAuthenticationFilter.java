package com.skillsync.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secretKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // ✅ Public endpoints
            if (path.contains("/v3/api-docs")
                    || path.contains("/swagger-ui")
                    || path.contains("/swagger-resources")
                    || path.contains("/webjars")
                    || path.contains("/auth/")) {
                return chain.filter(exchange);
            }

            // 🔐 Authorization Header check
            if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSignKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject();
                String role  = claims.get("role", String.class);

                // ❗ Safety check
                if (role == null) {
                    return onError(exchange, "Role not found in token", HttpStatus.UNAUTHORIZED);
                }

                // 🔒 ROLE-BASED ACCESS

                if (path.contains("/users") && !role.equals("ROLE_ADMIN")) {
                    return onError(exchange, "Access Denied - Admin Only", HttpStatus.FORBIDDEN);
                }

                if (path.contains("/mentors") &&
                        !(role.equals("ROLE_MENTOR") || role.equals("ROLE_ADMIN"))) {
                    return onError(exchange, "Access Denied - Mentor Only", HttpStatus.FORBIDDEN);
                }

                if (path.contains("/sessions") &&
                        !(role.equals("ROLE_USER") ||
                                role.equals("ROLE_MENTOR") ||
                                role.equals("ROLE_ADMIN"))) {
                    return onError(exchange, "Access Denied - Session Access", HttpStatus.FORBIDDEN);
                }

                // ✅ Forward headers
                ServerWebExchange mutated = exchange.mutate()
                        .request(r -> r
                                .header("Authorization", authHeader)
                                .header("X-User-Email", email != null ? email : "")
                                .header("X-User-Role", role))
                        .build();

                return chain.filter(mutated);

            } catch (Exception e) {
                return onError(exchange, "Invalid or Expired JWT Token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        var buffer = exchange.getResponse().bufferFactory()
                .wrap(("{\"error\":\"" + err + "\"}").getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}