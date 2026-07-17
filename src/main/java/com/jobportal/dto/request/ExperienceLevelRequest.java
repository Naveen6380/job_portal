package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExperienceLevelRequest {

    @NotBlank(message = "Label is required")
    private String label;

    private Integer minYears = 0;
    private Integer maxYears = 0;
}
