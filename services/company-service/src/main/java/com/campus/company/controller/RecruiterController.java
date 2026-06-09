package com.campus.company.controller;

import com.campus.company.dto.request.UpdateRecruiterRequest;
import com.campus.company.dto.response.RecruiterResponse;
import com.campus.company.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;

    @GetMapping("/me")
    public ResponseEntity<RecruiterResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(recruiterService.getMyProfile(UUID.fromString(userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecruiterResponse> updateProfile(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateRecruiterRequest request) {
        return ResponseEntity.ok(
                recruiterService.updateProfile(id, UUID.fromString(userId), role, request));
    }
}