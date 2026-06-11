package com.campus.job.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class StudentProfileDto {

    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private BigDecimal cgpa;
    private String branch;
    private Integer graduationYear;
    private Integer backlogs;
}
