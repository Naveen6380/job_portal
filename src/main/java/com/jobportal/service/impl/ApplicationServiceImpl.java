package com.jobportal.service.impl;

import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.ResumeFile;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.ResumeFileRepository;
import com.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ResumeFileRepository resumeFileRepository;

    @Override
    public List<ApplicationResponse> getApplicantsForJob(Long jobId, Long employerUserId) {
        List<Application> applications = applicationRepository.findByJobId(jobId);
        if (!applications.isEmpty()) {
            verifyOwnership(applications.get(0), employerUserId);
        }
        return applications.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getAllApplicantsForEmployer(Long employerUserId) {
        // Note: relies on Company -> Jobs -> Applications chain; company resolved via job.company.user
        return applicationRepository.findAll().stream()
                .filter(app -> app.getJob().getCompany().getUser().getId().equals(employerUserId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, Long employerUserId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        verifyOwnership(application, employerUserId);

        application.setStatus(request.getStatus().toUpperCase());
        application = applicationRepository.save(application);

        // TODO Day 4: EmailService + NotificationService -> notify candidate of status change

        return toResponse(application);
    }

    @Override
    public String getResumeUrlForApplication(Long applicationId, Long employerUserId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        verifyOwnership(application, employerUserId);

        if (application.getResumeId() == null) {
            throw new ResourceNotFoundException("No resume attached to this application");
        }
        ResumeFile resume = resumeFileRepository.findById(application.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume file not found"));
        return resume.getFileUrl();
    }

    private void verifyOwnership(Application application, Long employerUserId) {
        if (!application.getJob().getCompany().getUser().getId().equals(employerUserId)) {
            throw new AccessDeniedException("You do not have access to this application");
        }
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getUser().getFullName())
                .candidateEmail(application.getCandidate().getUser().getEmail())
                .candidatePhone(application.getCandidate().getUser().getPhone())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .resumeMatchScore(application.getResumeMatchScore())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}