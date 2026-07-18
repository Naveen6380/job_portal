package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;
    private String designation;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent = false;
    private String description;
}