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
public class InterviewCancelledEvent {

    /** The interview that was cancelled. */
    private UUID interviewId;

    /** The application this interview belonged to. */
    private UUID applicationId;

    /** The student whose interview was cancelled. */
    private UUID studentId;

    /** Student's full name — for email salutation. */
    private String studentName;

    /** Student's email — cancellation notification goes here. */
    private String studentEmail;

    /** Job title — included in notification subject. */
    private String jobTitle;

    /** Company name — included in notification body. */
    private String companyName;

    /**
     * The original date/time the interview was scheduled for (UTC).
     * Helps the student identify which interview was cancelled in the notification.
     */
    private Instant scheduledAt;

    /**
     * Reason for cancellation provided by the recruiter.
     * e.g. "Position has been filled internally."
     * Null if no reason was provided (valid, though poor UX).
     */
    private String reason;

    /** UTC timestamp when the cancellation was recorded in the system. */
    private Instant timestamp;
}
