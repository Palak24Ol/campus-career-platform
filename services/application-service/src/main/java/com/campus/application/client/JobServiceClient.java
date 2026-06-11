package com.campus.application.client;

import com.campus.application.dto.response.EligibilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "job-service", fallback = JobServiceClientFallback.class)
public interface JobServiceClient {

    @GetMapping("/jobs/{jobId}/eligibility-check")
    EligibilityResponse checkEligibility(@PathVariable UUID jobId,
                                         @RequestParam UUID studentId);
}