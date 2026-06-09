package com.campus.student.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StudentResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private BigDecimal cgpa;
    private String branch;
    private Integer graduationYear;
    private Integer backlogs;
    private String bio;
    private String[] skills;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    private List<EducationResponse> educations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
