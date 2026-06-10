package com.campus.interview.client;

import com.campus.interview.dto.response.ApplicationStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "application-service", fallback = ApplicationServiceClientFallback.class)
public interface ApplicationServiceClient {

    @GetMapping("/applications/{id}")
    ApplicationStatusDto getApplication(@PathVariable UUID id);
}
