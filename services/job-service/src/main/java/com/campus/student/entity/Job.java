package com.campus.job.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(length = 255)
    private String location;

    private Long ctc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    @Column(name = "min_cgpa", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal minCgpa = BigDecimal.ZERO;

    @Column(name = "eligible_branches", columnDefinition = "TEXT[]")
    @Builder.Default
    private String[] eligibleBranches = new String[0];

    @Column(name = "backlogs_allowed", nullable = false)
    @Builder.Default
    private boolean backlogsAllowed = true;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    private LocalDate deadline;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum JobType {
        INTERNSHIP, FULLTIME
    }

    public enum JobStatus {
        OPEN, CLOSED
    }
}
