package com.jobportal.service;

import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getApplicantsForJob(Long jobId, Long employerUserId);
    List<ApplicationResponse> getAllApplicantsForEmployer(Long employerUserId);
    ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, Long employerUserId);
    String getResumeUrlForApplication(Long applicationId, Long employerUserId);
}