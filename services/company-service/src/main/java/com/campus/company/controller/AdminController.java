package com.campus.company.controller;

import com.campus.company.dto.request.RejectCompanyRequest;
import com.campus.company.dto.response.CompanyResponse;
import com.campus.company.dto.response.RecruiterResponse;
import com.campus.company.exception.AccessDeniedException;
import com.campus.company.service.AdminApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminApprovalService adminApprovalService;

    @GetMapping("/companies/pending")
    public ResponseEntity<List<CompanyResponse>> getPendingCompanies(
            @RequestHeader("X-User-Role") String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminApprovalService.getPendingCompanies());
    }

    @PutMapping("/companies/{id}/approve")
    public ResponseEntity<CompanyResponse> approveCompany(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminApprovalService.approveCompany(id));
    }

    @PutMapping("/companies/{id}/reject")
    public ResponseEntity<CompanyResponse> rejectCompany(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody RejectCompanyRequest request) {
        assertAdmin(role);
        return ResponseEntity.ok(adminApprovalService.rejectCompany(id, request.getReason()));
    }

    @GetMapping("/recruiters/pending")
    public ResponseEntity<List<RecruiterResponse>> getPendingRecruiters(
            @RequestHeader("X-User-Role") String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminApprovalService.getPendingRecruiters());
    }

    @PutMapping("/recruiters/{id}/verify")
    public ResponseEntity<RecruiterResponse> verifyRecruiter(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminApprovalService.verifyRecruiter(id));
    }

    private void assertAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException();
        }
    }
}