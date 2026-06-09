package com.campus.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Minimal Spring Security configuration for the API Gateway.
 *
 * <h2>Design rationale</h2>
 * The Gateway has Spring Security on the classpath (via spring-boot-starter-actuator
 * and because spring-cloud-starter-gateway pulls it in transitively).
 * Without this configuration, Spring Security's default behavior would:
 * <ul>
 *   <li>Auto-generate a random password and require HTTP Basic Auth on every request</li>
 *   <li>Enable CSRF protection — which breaks REST API calls from Postman / mobile apps</li>
 *   <li>Block WebSocket upgrade requests</li>
 * </ul>
 *
 * <h2>Our JWT strategy</h2>
 * We do NOT use Spring Security's built-in JWT / OAuth2 resource server support here.
 * Instead, {@link com.campus.gateway.filter.JwtAuthenticationFilter} handles all JWT
 * validation as a plain {@link org.springframework.cloud.gateway.filter.GlobalFilter}.
 *
 * <p>Reasons for this choice:
 * <ol>
 *   <li>Simpler: No SecurityContext, no Authentication objects, no reactive security chain complexity.</li>
 *   <li>Consistent: Auth Service uses the same JJWT parsing code — one place to maintain.</li>
 *   <li>Controllable: We decide exactly what headers to pass downstream (X-User-Id, X-User-Role).</li>
 * </ol>
 *
 * <p>This means we tell Spring Security to permit all requests (our filter handles the real security).
 * Spring Security is effectively a no-op here — CSRF disabled, all paths permitted.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // ── Disable CSRF ─────────────────────────────────────────
                // REST APIs use stateless JWT, not session cookies.
                // CSRF protection only makes sense for browser session-cookie auth.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ── Disable HTTP Basic Auth ───────────────────────────────
                // We don't want Spring Security's auto-generated password prompt.
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // ── Disable Form Login ────────────────────────────────────
                // No login page — this is a REST/WS-only gateway.
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // ── Permit all requests ───────────────────────────────────
                // Our JwtAuthenticationFilter handles the actual authentication.
                // Spring Security just gets out of the way.
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())

                .build();
    }
}
