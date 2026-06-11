package com.campus.job.dto.request;

import com.campus.job.entity.Job;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateJobRequest {

    @Size(max = 255)
    private String title;

    private String description;

    @Size(max = 255)
    private String location;

    private Long ctc;

    private Job.JobStatus status;

    private BigDecimal minCgpa;

    private List<String> eligibleBranches;

    private Boolean backlogsAllowed;

    private Integer graduationYear;

    private LocalDate deadline;
}
