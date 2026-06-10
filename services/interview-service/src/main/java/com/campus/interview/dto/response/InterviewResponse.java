package com.campus.interview.dto.response;

import com.campus.interview.entity.Interview;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InterviewResponse {

    private UUID id;
    private UUID applicationId;
    private UUID studentId;
    private UUID jobId;
    private UUID companyId;
    private String companyName;
    private LocalDateTime scheduledAt;
    private Interview.InterviewMode mode;
    private String meetLink;
    private String venue;
    private Integer round;
    private Interview.InterviewStatus status;
    private String description;
    private String rescheduleReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
