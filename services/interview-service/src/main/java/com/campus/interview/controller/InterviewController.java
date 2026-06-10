package com.campus.interview.controller;

import com.campus.interview.dto.request.CancelInterviewRequest;
import com.campus.interview.dto.request.RescheduleInterviewRequest;
import com.campus.interview.dto.request.ScheduleInterviewRequest;
import com.campus.interview.dto.response.InterviewResponse;
import com.campus.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponse> schedule(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody ScheduleInterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.schedule(UUID.fromString(userId), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(interviewService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<InterviewResponse>> getByStudent(
            @PathVariable UUID studentId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(interviewService.getByStudentId(studentId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<InterviewResponse>> getByApplication(
            @PathVariable UUID applicationId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(interviewService.getByApplicationId(applicationId));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<InterviewResponse> reschedule(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody RescheduleInterviewRequest request) {
        return ResponseEntity.ok(
                interviewService.reschedule(id, UUID.fromString(userId), role, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InterviewResponse> cancel(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CancelInterviewRequest request) {
        return ResponseEntity.ok(
                interviewService.cancel(id, UUID.fromString(userId), role, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<InterviewResponse> complete(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(interviewService.complete(id, role));
    }
}
