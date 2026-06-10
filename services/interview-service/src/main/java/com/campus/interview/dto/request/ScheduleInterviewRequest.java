package com.campus.interview.dto.request;

import com.campus.interview.entity.Interview;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleInterviewRequest {

    @NotNull
    private UUID applicationId;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID jobId;

    @NotNull
    private UUID companyId;

    private String companyName;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotNull
    private Interview.InterviewMode mode;

    private String meetLink;

    private String venue;

    private Integer round;

    private String description;
}
