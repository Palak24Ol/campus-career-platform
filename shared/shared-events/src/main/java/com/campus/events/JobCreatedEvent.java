package com.campus.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCreatedEvent {

    /** The unique identifier of the newly created job. */
    private UUID jobId;

    /** Job title, e.g. "Software Engineer Intern". */
    private String title;

    /** The company that posted this job. */
    private UUID companyId;

    /**
     * Denormalized company name.
     * Avoids a Company Service lookup in the consumer.
     */
    private String companyName;

    /** Job location, e.g. "Bangalore, India" or "Remote". */
    private String location;

    /**
     * Job category.
     * Values: "INTERNSHIP" | "FULLTIME"
     * Using String (not enum) for forward compatibility —
     * adding a new type won't break old consumers.
     */
    private String type;

    /**
     * CTC in thousands per annum.
     * e.g. 1200 = 12 LPA. Null for internships (stipend handled separately).
     */
    private Long ctc;

    // ── Eligibility criteria (denormalized from Job entity) ────────────────

    /**
     * Minimum CGPA required to apply.
     * e.g. 7.5 means students with CGPA < 7.5 cannot apply.
     * 0.0 means no minimum — all students eligible.
     */
    private Double minCgpa;

    /**
     * Branches allowed to apply.
     * e.g. ["CSE", "IT", "ECE"]
     * Empty list means all branches are eligible.
     */
    private List<String> eligibleBranches;

    /**
     * Whether students with active backlogs can apply.
     * true = backlogs allowed | false = no active backlogs
     */
    private Boolean backlogsAllowed;

    /**
     * Target graduation year.
     * e.g. 2026 means only 2026 graduating batch is eligible.
     * Null means no restriction.
     */
    private Integer graduationYear;

    /**
     * Event creation timestamp (UTC).
     * Always set by the producer before publishing.
     */
    private Instant timestamp;
}
