package com.campus.student.controller;

import com.campus.student.dto.response.ResumeUploadResponse;
import com.campus.student.service.ResumeService;
import com.campus.student.service.StudentService;
import com.campus.student.dto.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final StudentService studentService;

    @PostMapping(value = "/{id}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam("file") MultipartFile file) {

        StudentResponse profile = studentService.getById(id);

        if (!role.equals("ADMIN") && !profile.getUserId().equals(UUID.fromString(userId))) {
            return ResponseEntity.status(403).build();
        }

        if (profile.getResumeUrl() != null) {
            resumeService.deleteResume(profile.getResumeUrl());
        }

        String objectKey = resumeService.uploadResume(UUID.fromString(userId), file);
        studentService.updateResumeUrl(id, objectKey);

        String presignedUrl = resumeService.generatePresignedUrl(objectKey);

        return ResponseEntity.ok(ResumeUploadResponse.builder()
                .resumeUrl(presignedUrl)
                .fileName(file.getOriginalFilename())
                .sizeBytes(file.getSize())
                .build());
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<String> getResumeUrl(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        StudentResponse profile = studentService.getById(id);

        if (profile.getResumeUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        String presignedUrl = resumeService.generatePresignedUrl(profile.getResumeUrl());
        return ResponseEntity.ok(presignedUrl);
    }
}
