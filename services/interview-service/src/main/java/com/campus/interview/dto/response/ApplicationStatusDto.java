package com.campus.interview.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ApplicationStatusDto {

    private UUID id;
    private UUID studentId;
    private UUID jobId;
    private UUID companyId;
    private String companyName;
    private String jobTitle;
    private String status;
}
