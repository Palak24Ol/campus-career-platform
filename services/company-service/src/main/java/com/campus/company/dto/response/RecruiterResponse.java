package com.campus.company.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RecruiterResponse {

    private UUID id;
    private UUID userId;
    private UUID companyId;
    private String companyName;
    private String name;
    private String email;
    private String phone;
    private String designation;
    private boolean verified;
    private LocalDateTime createdAt;
}