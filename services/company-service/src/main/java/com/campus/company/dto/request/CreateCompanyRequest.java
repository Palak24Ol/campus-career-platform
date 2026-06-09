package com.campus.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCompanyRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String website;

    private String description;

    @Size(max = 100)
    private String industry;

    @Size(max = 500)
    private String logoUrl;
}