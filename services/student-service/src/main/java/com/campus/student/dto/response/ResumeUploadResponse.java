package com.campus.student.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeUploadResponse {

    private String resumeUrl;
    private String fileName;
    private long sizeBytes;
}
