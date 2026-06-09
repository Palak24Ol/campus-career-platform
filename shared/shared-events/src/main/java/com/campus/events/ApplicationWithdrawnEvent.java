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
public class ApplicationWithdrawnEvent {

    /** The application that was withdrawn. */
    private UUID applicationId;

    /** The student who withdrew. */
    private UUID studentId;

    /** Student's full name — used in notification to recruiter. */
    private String studentName;

    /** Student's email — used for withdrawal confirmation. */
    private String studentEmail;

    /** The job this application was for. */
    private UUID jobId;

    /** Job title — used in notification body. */
    private String jobTitle;

    /** Company name — used in notification body. */
    private String companyName;

    /** Company identifier — used for routing recruiter notification. */
    private UUID companyId;

    /** UTC timestamp of the withdrawal. */
    private Instant timestamp;
}
