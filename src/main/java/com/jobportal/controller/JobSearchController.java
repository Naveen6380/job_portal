package com.jobportal.controller;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.JobSkillRepository;
import com.jobportal.repository.specification.JobSpecification;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Public - Job Search", description = "Search and filter job listings (no login required)")
public class JobSearchController {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long qualificationId,
            @RequestParam(required = false) Long experienceLevelId,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Specification<Job> spec = Specification.where(JobSpecification.isActive())
                .and(JobSpecification.hasKeyword(keyword))
                .and(JobSpecification.hasCategory(categoryId))
                .and(JobSpecification.hasLocation(locationId))
                .and(JobSpecification.hasCompany(companyId))
                .and(JobSpecification.hasQualification(qualificationId))
                .and(JobSpecification.hasExperienceLevel(experienceLevelId))
                .and(JobSpecification.hasJobType(jobType))
                .and(JobSpecification.hasWorkMode(workMode))
                .and(JobSpecification.minSalaryAtLeast(minSalary));

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Job> jobs = jobRepository.findAll(spec, pageable);
        Page<JobResponse> response = jobs.map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.success("Jobs fetched", response));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getFeaturedJobs() {
        Specification<Job> spec = Specification.where(JobSpecification.isActive())
                .and((root, query, cb) -> cb.isTrue(root.get("isFeatured")));
        List<JobResponse> jobs = jobRepository.findAll(spec).stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Featured jobs fetched", jobs));
    }

    private JobResponse toResponse(Job job) {
        List<String> skills = jobSkillRepository.findByJobId(job.getId()).stream()
                .map(js -> js.getSkill().getName())
                .collect(Collectors.toList());

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompany().getCompanyName())
                .companyLogoUrl(job.getCompany().getLogoUrl())
                .category(job.getCategory() != null ? job.getCategory().getName() : null)
                .designation(job.getDesignation() != null ? job.getDesignation().getTitle() : null)
                .location(job.getLocation() != null ? job.getLocation().getCity() : null)
                .qualification(job.getQualification() != null ? job.getQualification().getName() : null)
                .experienceLevel(job.getExperienceLevel() != null ? job.getExperienceLevel().getLabel() : null)
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .jobType(job.getJobType())
                .workMode(job.getWorkMode())
                .vacancies(job.getVacancies())
                .status(job.getStatus())
                .isFeatured(job.getIsFeatured())
                .viewsCount(job.getViewsCount())
                .expiresAt(job.getExpiresAt())
                .skills(skills)
                .createdAt(job.getCreatedAt())
                .build();
    }
}