package com.campus.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {


    @Value("${jwt.secret}")
    private String jwtSecret;


    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/actuator"          // actuator endpoints — no auth needed
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Step 1: Skip public paths
        if (isPublicPath(path)) {
            log.debug("Public path — skipping JWT validation: {}", path);
            return chain.filter(exchange);
        }

        // Step 2: Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header for path: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        // Step 3: Validate JWT and extract claims
        Claims claims;
        try {
            claims = parseAndValidateToken(token);
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired for path: {} — exp: {}", path, ex.getClaims().getExpiration());
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token has expired");
        } catch (JwtException ex) {
            log.warn("Invalid JWT for path: {} — {}", path, ex.getMessage());
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
        } catch (Exception ex) {
            log.error("Unexpected error validating JWT for path: {}", path, ex);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token validation failed");
        }

        //Step 4: Extract userId and role from claims
        String userId = claims.getSubject();
        String role   = claims.get("role", String.class);
        String email  = claims.get("email", String.class);

        if (userId == null || role == null) {
            log.warn("JWT missing required claims (sub or role) for path: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token missing required claims");
        }

        log.debug("JWT valid — userId: {}, role: {}, path: {}", userId, role, path);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id",    userId)
                .header("X-User-Role",  role)
                .header("X-User-Email", email != null ? email : "")
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    //  Private helpers

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }


    private Claims parseAndValidateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private Mono<Void> writeErrorResponse(ServerWebExchange exchange,
                                          HttpStatus status,
                                          String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status.value(),
                status.getReasonPhrase(),
                message
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
