package com.campus.student.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class EducationResponse {

    private UUID id;
    private String degree;
    private String institution;
    private Integer startYear;
    private Integer endYear;
    private BigDecimal grade;
}
