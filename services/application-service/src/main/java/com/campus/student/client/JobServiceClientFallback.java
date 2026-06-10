package com.campus.application.client;

import com.campus.application.dto.response.EligibilityResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JobServiceClientFallback implements JobServiceClient {

    @Override
    public EligibilityResponse checkEligibility(UUID jobId, UUID studentId) {
        return EligibilityResponse.builder()
                .eligible(false)
                .reason("Job service is currently unavailable. Please try again later.")
                .build();
    }
}