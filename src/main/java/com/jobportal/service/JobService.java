package com.jobportal.service;

import com.jobportal.dto.request.CreateJobRequest;
import com.jobportal.dto.response.JobResponse;

import java.util.List;

public interface JobService {
    JobResponse createJob(CreateJobRequest request, Long employerUserId);
    JobResponse updateJob(Long jobId, CreateJobRequest request, Long employerUserId);
    void deleteJob(Long jobId, Long employerUserId);
    JobResponse toggleJobStatus(Long jobId, Long employerUserId, String newStatus);
    List<JobResponse> getJobsByEmployer(Long employerUserId);
    JobResponse getJobById(Long jobId);
}