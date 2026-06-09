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
public class StudentShortlistedEvent {

    /** The application that was shortlisted. */
    private UUID applicationId;

    /** The student being shortlisted. */
    private UUID studentId;

    /** Student's full name — for email salutation. */
    private String studentName;

    /** Student's email — Notification Service sends email here. */
    private String studentEmail;

    /** The job this application is for. */
    private UUID jobId;

    /** Job title — included in notification subject and body. */
    private String jobTitle;

    /** Company that shortlisted this student. */
    private UUID companyId;

    /** Company name — included in notification body. */
    private String companyName;

    /**
     * Optional message from recruiter to the student.
     * e.g. "Please be ready for a technical screening round."
     * Null if recruiter did not add a note.
     */
    private String recruiterNote;

    /** UTC timestamp of the shortlisting action. */
    private Instant timestamp;
}
