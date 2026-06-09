package com.campus.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String method = request.getMethod().name();
        String path   = request.getURI().getPath();

        // X-User-Id is set by JwtAuthenticationFilter (which runs before this)
        String userId = request.getHeaders().getFirst("X-User-Id");
        String role   = request.getHeaders().getFirst("X-User-Role");

        String userInfo = (userId != null)
                ? "userId: " + userId + ", role: " + role
                : "anonymous";

        long startTime = System.currentTimeMillis();

        log.info("--> {} {} [{}]", method, path, userInfo);

        // Mono.fromRunnable runs after the downstream filter chain completes
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    log.info("<-- {} {} [{}] {} in {}ms",
                            method, path, userInfo, statusCode, duration);
                }));
    }

    @Override
    public int getOrder() {
        // Runs after JwtAuthenticationFilter (HIGHEST_PRECEDENCE + 1)
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
