package com.campus.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubmitApplicationRequest {

    @NotNull
    private UUID jobId;

    @NotBlank
    private String jobTitle;

    @NotBlank
    private String companyName;

    @NotNull
    private UUID companyId;

    private String studentName;
}