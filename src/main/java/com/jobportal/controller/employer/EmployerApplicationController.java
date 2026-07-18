package com.jobportal.controller.employer;

import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer/applications")
@RequiredArgsConstructor
@Tag(name = "Employer - Applicants", description = "View applicants, shortlist/reject candidates, download resumes")
public class EmployerApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAllApplicants(
            @AuthenticationPrincipal UserPrincipal employer) {
        return ResponseEntity.ok(ApiResponse.success(
                "Applicants fetched", applicationService.getAllApplicantsForEmployer(employer.getId())));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicantsForJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal employer) {
        return ResponseEntity.ok(ApiResponse.success(
                "Applicants fetched", applicationService.getApplicantsForJob(jobId, employer.getId())));
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal UserPrincipal employer) {
        ApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request, employer.getId());
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }

    @GetMapping("/{applicationId}/resume")
    public ResponseEntity<ApiResponse<String>> getResume(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserPrincipal employer) {
        String url = applicationService.getResumeUrlForApplication(applicationId, employer.getId());
        return ResponseEntity.ok(ApiResponse.success("Resume URL fetched", url));
    }
}