package com.campus.job.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilityResponse {

    private boolean eligible;
    private String reason;
}
