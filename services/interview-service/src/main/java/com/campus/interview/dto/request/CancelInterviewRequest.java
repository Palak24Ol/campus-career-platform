package com.campus.interview.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelInterviewRequest {

    @NotBlank
    private String reason;
}
