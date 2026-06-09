package com.campus.company.controller;

import com.campus.company.dto.request.CreateCompanyRequest;
import com.campus.company.dto.request.UpdateCompanyRequest;
import com.campus.company.dto.response.CompanyResponse;
import com.campus.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @Valid @RequestBody CreateCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(UUID.fromString(userId), email, request));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllApproved() {
        return ResponseEntity.ok(companyService.getAllApprovedCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateCompanyRequest request) {
        return ResponseEntity.ok(
                companyService.updateCompany(id, UUID.fromString(userId), role, request));
    }
}