package com.campus.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectCompanyRequest {

    @NotBlank
    private String reason;
}