package com.campus.job.dto.response;

import com.campus.job.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobResponse {

    private UUID id;
    private String title;
    private String description;
    private UUID companyId;
    private String companyName;
    private String location;
    private Long ctc;
    private Job.JobType type;
    private Job.JobStatus status;
    private BigDecimal minCgpa;
    private String[] eligibleBranches;
    private boolean backlogsAllowed;
    private Integer graduationYear;
    private LocalDate deadline;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
