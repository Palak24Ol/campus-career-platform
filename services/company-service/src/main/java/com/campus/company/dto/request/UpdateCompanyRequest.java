package com.campus.company.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCompanyRequest {

    @Size(max = 255)
    private String website;

    private String description;

    @Size(max = 100)
    private String industry;

    @Size(max = 500)
    private String logoUrl;
}