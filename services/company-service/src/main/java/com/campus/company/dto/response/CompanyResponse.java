package com.campus.company.dto.response;

import com.campus.company.entity.Company;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompanyResponse {

    private UUID id;
    private String name;
    private String website;
    private String description;
    private String industry;
    private String logoUrl;
    private Company.CompanyStatus status;
    private String rejectionReason;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}