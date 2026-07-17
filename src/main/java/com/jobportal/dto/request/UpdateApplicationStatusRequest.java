package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;   // SHORTLISTED, INTERVIEW_SCHEDULED, SELECTED, REJECTED
}