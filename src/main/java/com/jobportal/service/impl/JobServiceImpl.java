package com.jobportal.service.impl;

import com.jobportal.dto.request.CreateJobRequest;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.entity.*;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.*;
import com.jobportal.repository.JobSkillRepository;
import com.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final DesignationRepository designationRepository;
    private final LocationRepository locationRepository;
    private final QualificationRepository qualificationRepository;
    private final ExperienceLevelRepository experienceLevelRepository;
    private final SkillRepository skillRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public JobResponse createJob(CreateJobRequest request, Long employerUserId) {
        Company company = companyRepository.findByUserId(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for this employer"));

        if (!"APPROVED".equals(company.getVerificationStatus())) {
            throw new AccessDeniedException("Your company is not yet verified by admin. You cannot post jobs.");
        }

        Job job = Job.builder()
                .company(company)
                .postedBy(employerUserId)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(getCategory(request.getCategoryId()))
                .designation(request.getDesignationId() != null ? getDesignation(request.getDesignationId()) : null)
                .location(getLocation(request.getLocationId()))
                .qualification(request.getQualificationId() != null ? getQualification(request.getQualificationId()) : null)
                .experienceLevel(request.getExperienceLevelId() != null ? getExperienceLevel(request.getExperienceLevelId()) : null)
                .minSalary(request.getMinSalary())
                .maxSalary(request.getMaxSalary())
                .jobType(request.getJobType())
                .workMode(request.getWorkMode())
                .vacancies(request.getVacancies())
                .status("ACTIVE")
                .expiresAt(request.getExpiresAt())
                .build();

        job = jobRepository.save(job);
        attachSkills(job, request.getSkillIds());

        return toResponse(job);
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, CreateJobRequest request, Long employerUserId) {
        Job job = getOwnedJob(jobId, employerUserId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCategory(getCategory(request.getCategoryId()));
        job.setDesignation(request.getDesignationId() != null ? getDesignation(request.getDesignationId()) : null);
        job.setLocation(getLocation(request.getLocationId()));
        job.setQualification(request.getQualificationId() != null ? getQualification(request.getQualificationId()) : null);
        job.setExperienceLevel(request.getExperienceLevelId() != null ? getExperienceLevel(request.getExperienceLevelId()) : null);
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setJobType(request.getJobType());
        job.setWorkMode(request.getWorkMode());
        job.setVacancies(request.getVacancies());
        job.setExpiresAt(request.getExpiresAt());

        job = jobRepository.save(job);

        jobSkillRepository.deleteByJobId(job.getId());
        attachSkills(job, request.getSkillIds());

        return toResponse(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, Long employerUserId) {
        Job job = getOwnedJob(jobId, employerUserId);
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public JobResponse toggleJobStatus(Long jobId, Long employerUserId, String newStatus) {
        Job job = getOwnedJob(jobId, employerUserId);
        job.setStatus(newStatus.toUpperCase());
        return toResponse(jobRepository.save(job));
    }

    @Override
    public List<JobResponse> getJobsByEmployer(Long employerUserId) {
        Company company = companyRepository.findByUserId(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for this employer"));
        return jobRepository.findByCompanyId(company.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        job.setViewsCount(job.getViewsCount() + 1);
        jobRepository.save(job);
        return toResponse(job);
    }

    // ---------- helpers ----------

    private Job getOwnedJob(Long jobId, Long employerUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!job.getCompany().getUser().getId().equals(employerUserId)) {
            throw new AccessDeniedException("You do not own this job posting");
        }
        return job;
    }

    private void attachSkills(Job job, List<Long> skillIds) {
        if (skillIds == null) return;
        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
            jobSkillRepository.save(JobSkill.builder().job(job).skill(skill).isMandatory(true).build());
        }
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private Designation getDesignation(Long id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));
    }

    private Location getLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    private Qualification getQualification(Long id) {
        return qualificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + id));
    }

    private ExperienceLevel getExperienceLevel(Long id) {
        return experienceLevelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience level not found with id: " + id));
    }

    private JobResponse toResponse(Job job) {
        List<String> skillNames = jobSkillRepository.findByJobId(job.getId()).stream()
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
                .skills(skillNames)
                .applicationsCount(applicationRepository.findByJobId(job.getId()).size())
                .createdAt(job.getCreatedAt())
                .build();
    }
}