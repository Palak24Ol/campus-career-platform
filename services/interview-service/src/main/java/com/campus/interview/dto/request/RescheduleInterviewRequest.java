package com.campus.interview.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleInterviewRequest {

    @NotNull
    private LocalDateTime newScheduledAt;

    @NotBlank
    private String reason;

    private String meetLink;

    private String venue;
}
