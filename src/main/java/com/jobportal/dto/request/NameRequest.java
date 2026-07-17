package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Reused for any master-data entity that is just a unique "name" field:
 * Category, Skill, Qualification.
 */
@Data
public class NameRequest {

    @NotBlank(message = "Name is required")
    private String name;
}
