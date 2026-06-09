package com.campus.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferReleasedEvent {

    /** The application that resulted in this offer. */
    private UUID applicationId;

    /** The student receiving the offer. */
    private UUID studentId;

    /** Student's full name — for email salutation and Analytics records. */
    private String studentName;

    /** Student's email — offer congratulations email goes here. */
    private String studentEmail;

    /** The job for which the offer is being made. */
    private UUID jobId;

    /** Job title — included in offer notification. */
    private String jobTitle;

    /** The company making the offer. */
    private UUID companyId;

    /** Company name — included in email subject and body. */
    private String companyName;

    /**
     * Cost to company in thousands per annum.
     * 1200 = 12 LPA, 2500 = 25 LPA.
     * Analytics Service uses this to compute average and highest packages.
     */
    private Long ctc;

    /**
     * Expected joining date in ISO-8601 format: "YYYY-MM-DD".
     * e.g. "2026-07-01"
     * Null if not specified at the time of offer release.
     */
    private String joiningDate;

    /**
     * UTC timestamp when the offer was released.
     * Analytics Service uses this to determine which monthly trend bucket to update.
     */
    private Instant timestamp;
}
