package com.campus.interview.client;

import com.campus.interview.dto.response.ApplicationStatusDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApplicationServiceClientFallback implements ApplicationServiceClient {

    @Override
    public ApplicationStatusDto getApplication(UUID id) {
        return null;
    }
}
