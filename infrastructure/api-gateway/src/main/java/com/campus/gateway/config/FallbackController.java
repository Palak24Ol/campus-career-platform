package com.campus.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    @RequestMapping("/auth")
    public Mono<Map<String, Object>> authFallback(ServerWebExchange exchange) {
        return buildFallback("auth-service", "Authentication service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/student")
    public Mono<Map<String, Object>> studentFallback(ServerWebExchange exchange) {
        return buildFallback("student-service", "Student service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/company")
    public Mono<Map<String, Object>> companyFallback(ServerWebExchange exchange) {
        return buildFallback("company-service", "Company service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/job")
    public Mono<Map<String, Object>> jobFallback(ServerWebExchange exchange) {
        return buildFallback("job-service", "Job service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/application")
    public Mono<Map<String, Object>> applicationFallback(ServerWebExchange exchange) {
        return buildFallback("application-service", "Application service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/interview")
    public Mono<Map<String, Object>> interviewFallback(ServerWebExchange exchange) {
        return buildFallback("interview-service", "Interview service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/notification")
    public Mono<Map<String, Object>> notificationFallback(ServerWebExchange exchange) {
        return buildFallback("notification-service", "Notification service is temporarily unavailable.", exchange);
    }

    @RequestMapping("/analytics")
    public Mono<Map<String, Object>> analyticsFallback(ServerWebExchange exchange) {
        return buildFallback("analytics-service", "Analytics service is temporarily unavailable.", exchange);
    }

    // Helper
    private Mono<Map<String, Object>> buildFallback(String serviceName,
                                                     String message,
                                                     ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);

        String path = exchange.getRequest().getURI().getPath();
        log.warn("Circuit breaker triggered for {} — path: {}", serviceName, path);

        return Mono.just(Map.of(
                "status",    503,
                "error",     "Service Unavailable",
                "service",   serviceName,
                "message",   message + " Please try again in a few moments.",
                "timestamp", Instant.now().toString(),
                "path",      path
        ));
    }
}
