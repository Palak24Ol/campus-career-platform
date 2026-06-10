package com.campus.application.dto.response;

import com.campus.application.entity.Application;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {

    private UUID id;
    private UUID studentId;
    private UUID jobId;
    private String studentName;
    private String jobTitle;
    private String companyName;
    private UUID companyId;
    private Application.ApplicationStatus status;
    private String recruiterNote;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}