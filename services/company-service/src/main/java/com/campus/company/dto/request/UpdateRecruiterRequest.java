package com.campus.company.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRecruiterRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 15)
    private String phone;

    @Size(max = 100)
    private String designation;
}