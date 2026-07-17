package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String companyLogoUrl;
    private String category;
    private String designation;
    private String location;
    private String qualification;
    private String experienceLevel;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String jobType;
    private String workMode;
    private Integer vacancies;
    private String status;
    private Boolean isFeatured;
    private Integer viewsCount;
    private LocalDate expiresAt;
    private List<String> skills;
    private Integer applicationsCount;
    private LocalDateTime createdAt;
}