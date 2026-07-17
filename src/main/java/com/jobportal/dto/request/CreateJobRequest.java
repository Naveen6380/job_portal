package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Long designationId;

    @NotNull(message = "Location is required")
    private Long locationId;

    private Long qualificationId;
    private Long experienceLevelId;

    private BigDecimal minSalary;
    private BigDecimal maxSalary;

    private String jobType = "FULL_TIME";     // FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT
    private String workMode = "ONSITE";       // ONSITE, REMOTE, HYBRID
    private Integer vacancies = 1;
    private LocalDate expiresAt;

    private List<Long> skillIds;              // mandatory skills for this job
}