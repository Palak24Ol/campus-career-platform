package com.campus.application.dto.request;

import com.campus.application.entity.Application;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Application.ApplicationStatus status;

    private String recruiterNote;

    private Long ctc;

    private String joiningDate;
}