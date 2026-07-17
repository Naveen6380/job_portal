package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DesignationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private Long categoryId;   // optional link to a Category
}
