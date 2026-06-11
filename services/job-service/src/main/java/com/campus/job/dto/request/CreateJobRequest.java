package com.campus.job.dto.request;

import com.campus.job.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateJobRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private UUID companyId;

    @NotBlank
    private String companyName;

    @Size(max = 255)
    private String location;

    private Long ctc;

    @NotNull
    private Job.JobType type;

    private BigDecimal minCgpa;

    private List<String> eligibleBranches;

    private Boolean backlogsAllowed;

    private Integer graduationYear;

    private LocalDate deadline;
}
