package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank(message = "Project title is required")
    private String title;
    private String description;
    private String techStack;
    private String projectUrl;
}