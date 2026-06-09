package com.campus.student.controller;

import com.campus.student.dto.request.UpdateStudentRequest;
import com.campus.student.dto.response.StudentResponse;
import com.campus.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<StudentResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader(value = "X-User-Email", required = false) String email) {
        String fallbackEmail = (email != null && !email.isBlank()) ? email : userId;
        String fallbackName = fallbackEmail;
        return ResponseEntity.ok(
                studentService.getOrCreateProfile(UUID.fromString(userId), fallbackEmail, fallbackName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentResponse> getByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(studentService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateProfile(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(
                studentService.updateProfile(id, UUID.fromString(userId), role, request));
    }
}
