package com.campus.job.controller;

import com.campus.job.dto.response.EligibilityResponse;
import com.campus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class EligibilityController {

    private final JobService jobService;

    @GetMapping("/{id}/eligibility-check")
    public ResponseEntity<EligibilityResponse> checkEligibility(
            @PathVariable UUID id,
            @RequestParam UUID studentId) {
        return ResponseEntity.ok(jobService.checkEligibility(id, studentId));
    }
}
