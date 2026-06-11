package com.campus.analytics.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompanyStatsResponse {

    private UUID companyId;
    private String companyName;
    private Long totalApplications;
    private Long totalOffers;
    private BigDecimal averageCtc;
    private LocalDateTime updatedAt;
}
