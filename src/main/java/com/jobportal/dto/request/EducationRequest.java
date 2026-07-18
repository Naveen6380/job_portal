package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EducationRequest {
    @NotBlank(message = "Institution name is required")
    private String institutionName;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;
    private String grade;
}