package com.campus.job.client;

import com.campus.job.dto.response.StudentProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "student-service", fallback = StudentServiceClientFallback.class)
public interface StudentServiceClient {

    @GetMapping("/students/user/{userId}")
    StudentProfileDto getStudentByUserId(@PathVariable UUID userId);
}
