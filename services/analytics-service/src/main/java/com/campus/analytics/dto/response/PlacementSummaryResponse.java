package com.campus.analytics.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PlacementSummaryResponse {

    private Long totalApplications;
    private Long totalOffers;
    private Long totalActiveJobs;
    private BigDecimal averagePackage;
    private BigDecimal highestPackage;
    private LocalDateTime updatedAt;
}
