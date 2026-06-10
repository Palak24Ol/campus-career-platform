package com.campus.job.client;

import com.campus.job.dto.response.StudentProfileDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StudentServiceClientFallback implements StudentServiceClient {

    @Override
    public StudentProfileDto getStudentByUserId(UUID userId) {
        return null;
    }
}
