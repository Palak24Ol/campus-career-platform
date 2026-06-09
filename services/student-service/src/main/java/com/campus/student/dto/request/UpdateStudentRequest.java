package com.campus.student.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateStudentRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 15)
    private String phone;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal cgpa;

    @Size(max = 50)
    private String branch;

    private Integer graduationYear;

    private Integer backlogs;

    private String bio;

    private List<String> skills;

    private String linkedinUrl;

    private String githubUrl;

    private List<EducationRequest> educations;
}
