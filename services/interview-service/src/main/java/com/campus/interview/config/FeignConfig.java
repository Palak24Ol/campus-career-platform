package com.campus.interview.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor serviceHeaderInterceptor() {
        return template -> {
            template.header("X-User-Id", "service-internal");
            template.header("X-User-Role", "SERVICE");
        };
    }
}