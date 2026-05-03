package com.skillsync.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "mysecretkeymysecretleymysecretkey32";

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "secretKey", SECRET);
    }

    @Test
    void publicAuthRouteShouldSkipJwtValidation() {
        MockServerWebExchange exchange = exchange(HttpMethod.POST, "/auth/login", null);
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        applyFilter(exchange).filter(exchange, forwardedExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        }).block();

        assertTrue(chainCalled.get());
    }

    @Test
    void protectedRouteWithoutAuthorizationHeaderShouldReturnUnauthorized() {
        MockServerWebExchange exchange = exchange(HttpMethod.GET, "/sessions/1", null);

        applyFilter(exchange).filter(exchange, forwardedExchange -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void learnerShouldNotCreateUsersBecauseUsersWriteRoutesAreAdminOnly() {
        MockServerWebExchange exchange = exchange(HttpMethod.POST, "/users", token("learner@example.com", "ROLE_LEARNER"));

        applyFilter(exchange).filter(exchange, forwardedExchange -> Mono.empty()).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void validAdminTokenShouldForwardIdentityHeaders() {
        String token = token("admin@example.com", "ROLE_ADMIN");
        MockServerWebExchange exchange = exchange(HttpMethod.GET, "/admin/dashboard", token);
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        applyFilter(exchange).filter(exchange, forwardedExchange -> {
            forwarded.set(forwardedExchange);
            return Mono.empty();
        }).block();

        assertEquals("admin@example.com", forwarded.get().getRequest().getHeaders().getFirst("X-User-Email"));
        assertEquals("ROLE_ADMIN", forwarded.get().getRequest().getHeaders().getFirst("X-User-Role"));
        assertEquals("Bearer " + token, forwarded.get().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    private GatewayFilter applyFilter(MockServerWebExchange exchange) {
        return filter.apply(new JwtAuthenticationFilter.Config());
    }

    private MockServerWebExchange exchange(HttpMethod method, String path, String token) {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.method(method, path);
        if (token != null) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return MockServerWebExchange.from(request);
    }

    private String token(String subject, String role) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
