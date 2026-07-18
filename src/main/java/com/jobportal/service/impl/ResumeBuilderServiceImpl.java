package com.jobportal.service.impl;

import com.jobportal.dto.request.EducationRequest;
import com.jobportal.dto.request.ExperienceRequest;
import com.jobportal.dto.request.ProjectRequest;
import com.jobportal.entity.*;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.*;
import com.jobportal.service.ResumeBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeBuilderServiceImpl implements ResumeBuilderService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;

    // ---------- Education ----------

    @Override
    @Transactional
    public Education addEducation(Long userId, EducationRequest request) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Education education = Education.builder()
                .candidate(profile)
                .institutionName(request.getInstitutionName())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .grade(request.getGrade())
                .build();
        return educationRepository.save(education);
    }

    @Override
    public List<Education> getEducation(Long userId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        return educationRepository.findByCandidateId(profile.getId());
    }

    @Override
    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));
        if (!education.getCandidate().getId().equals(profile.getId())) {
            throw new AccessDeniedException("You do not own this education record");
        }
        educationRepository.delete(education);
    }

    // ---------- Experience ----------

    @Override
    @Transactional
    public Experience addExperience(Long userId, ExperienceRequest request) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Experience experience = Experience.builder()
                .candidate(profile)
                .companyName(request.getCompanyName())
                .designation(request.getDesignation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isCurrent(request.getIsCurrent())
                .description(request.getDescription())
                .build();
        return experienceRepository.save(experience);
    }

    @Override
    public List<Experience> getExperience(Long userId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        return experienceRepository.findByCandidateId(profile.getId());
    }

    @Override
    @Transactional
    public void deleteExperience(Long userId, Long experienceId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience record not found"));
        if (!experience.getCandidate().getId().equals(profile.getId())) {
            throw new AccessDeniedException("You do not own this experience record");
        }
        experienceRepository.delete(experience);
    }

    // ---------- Projects ----------

    @Override
    @Transactional
    public Project addProject(Long userId, ProjectRequest request) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Project project = Project.builder()
                .candidate(profile)
                .title(request.getTitle())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .projectUrl(request.getProjectUrl())
                .build();
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getProjects(Long userId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        return projectRepository.findByCandidateId(profile.getId());
    }

    @Override
    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        CandidateProfile profile = getOwnedCandidateProfile(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getCandidate().getId().equals(profile.getId())) {
            throw new AccessDeniedException("You do not own this project");
        }
        projectRepository.delete(project);
    }

    // ---------- helper ----------

    private CandidateProfile getOwnedCandidateProfile(Long userId) {
        return candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate profile not found. Please create your profile first."));
    }
}