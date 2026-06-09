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
public class InterviewScheduledEvent {

    /** Unique identifier of the interview record in interview_db. */
    private UUID interviewId;

    /** The application this interview belongs to. */
    private UUID applicationId;

    /** The student being interviewed. */
    private UUID studentId;

    /** Student's full name — for email salutation. */
    private String studentName;

    /** Student's email — Notification Service sends the interview invite here. */
    private String studentEmail;

    /** The job this interview is for. */
    private UUID jobId;

    /** Job title — included in email subject: "Interview for {jobTitle} at {companyName}". */
    private String jobTitle;

    /** Company conducting the interview. */
    private UUID companyId;

    /** Company name — included in email body. */
    private String companyName;

    /**
     * Scheduled date and time of the interview (UTC).
     * Notification Service formats this to the student's local timezone
     * based on their profile (future feature) or sends UTC with a note.
     */
    private Instant interviewDate;

    /**
     * Interview mode.
     * Values: "ONLINE" | "OFFLINE"
     */
    private String mode;

    /**
     * Google Meet / Zoom / Teams link.
     * Populated when mode = "ONLINE". Null for offline interviews.
     */
    private String meetLink;

    /**
     * Physical venue address.
     * Populated when mode = "OFFLINE". Null for online interviews.
     */
    private String venue;

    /**
     * Interview round number.
     * 1 = first round (usually technical screening)
     * 2 = second round (usually managerial)
     * 3 = HR round
     * etc.
     */
    private Integer round;

    /**
     * Instructions or description from the recruiter.
     * e.g. "Please bring a printed resume. Dress code: Business casual."
     * Null if no instructions provided.
     */
    private String description;

    /** UTC timestamp when the interview was scheduled in the system. */
    private Instant timestamp;
}
