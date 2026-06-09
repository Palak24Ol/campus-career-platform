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
public class ApplicationSubmittedEvent {

    /** Unique identifier of the application record in application_db. */
    private UUID applicationId;

    /** The student who applied. */
    private UUID studentId;

    /** Student's full name — used in email templates. */
    private String studentName;

    /** Student's email — Notification Service sends email here. */
    private String studentEmail;

    /** The job the student applied for. */
    private UUID jobId;

    /** Job title — used in email subject/body. */
    private String jobTitle;

    /** Company that posted the job. */
    private UUID companyId;

    /** Company name — used in email body and Analytics grouping. */
    private String companyName;

    /** Event creation timestamp (UTC).*/
    private Instant timestamp;
}
