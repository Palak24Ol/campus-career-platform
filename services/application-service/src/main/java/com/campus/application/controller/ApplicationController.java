package com.campus.application.controller;

import com.campus.application.dto.request.SubmitApplicationRequest;
import com.campus.application.dto.request.UpdateStatusRequest;
import com.campus.application.dto.response.ApplicationResponse;
import com.campus.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> submit(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody SubmitApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.submit(UUID.fromString(userId), request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponse>> getByStudent(
            @PathVariable UUID studentId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(applicationService.getByStudentId(studentId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getByJob(
            @PathVariable UUID jobId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(applicationService.getByJobId(jobId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(
                applicationService.updateStatus(id, UUID.fromString(userId), role, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId) {
        applicationService.withdraw(id, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}