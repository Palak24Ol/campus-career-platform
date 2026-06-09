package com.campus.student.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EducationRequest {

    private String degree;
    private String institution;
    private Integer startYear;
    private Integer endYear;
    private BigDecimal grade;
}
