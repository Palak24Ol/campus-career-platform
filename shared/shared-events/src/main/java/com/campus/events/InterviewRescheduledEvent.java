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
public class InterviewRescheduledEvent {

    /** The interview that was rescheduled. */
    private UUID interviewId;

    /** The application this interview belongs to. */
    private UUID applicationId;

    /** The student whose interview was rescheduled. */
    private UUID studentId;

    /** Student's full name — for email salutation. */
    private String studentName;

    /** Student's email — notification goes here. */
    private String studentEmail;

    /** Job title — included in email subject. */
    private String jobTitle;

    /** Company name — included in email body. */
    private String companyName;

    /**
     * The NEW scheduled date/time after reschedule (UTC).
     * This is the date the student should now prepare for.
     */
    private Instant newDate;

    /**
     * The ORIGINAL scheduled date/time before reschedule (UTC).
     * Included so the notification email can say "previously scheduled for X".
     */
    private Instant oldDate;

    /**
     * Reason provided by the recruiter for rescheduling.
     * e.g. "Interviewer unavailable on original date"
     * Null if no reason was provided.
     */
    private String reason;

    /**
     * Updated meet link if it changed along with the reschedule.
     * Null if the link remains the same.
     */
    private String meetLink;

    /**
     * Updated venue if it changed along with the reschedule.
     * Null if the venue remains the same.
     */
    private String venue;

    /** UTC timestamp when the reschedule action was recorded. */
    private Instant timestamp;
}
