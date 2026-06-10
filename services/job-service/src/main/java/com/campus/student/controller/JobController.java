package com.campus.job.controller;

import com.campus.job.dto.request.CreateJobRequest;
import com.campus.job.dto.request.UpdateJobRequest;
import com.campus.job.dto.response.JobResponse;
import com.campus.job.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(UUID.fromString(userId), request));
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getJobs(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(jobService.getOpenJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateJobRequest request) {
        return ResponseEntity.ok(
                jobService.updateJob(id, UUID.fromString(userId), role, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        jobService.deleteJob(id, UUID.fromString(userId), role);
        return ResponseEntity.noContent().build();
    }
}
