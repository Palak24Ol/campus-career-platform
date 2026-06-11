package com.campus.analytics.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyTrendResponse {

    private Integer year;
    private Integer month;
    private Long applications;
    private Long offers;
    private BigDecimal averageCtc;
}
