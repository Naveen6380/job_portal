package com.jobportal.controller.employer;

import com.jobportal.dto.request.CreateJobRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer/jobs")
@RequiredArgsConstructor
@Tag(name = "Employer - Jobs", description = "Post, update, delete, and manage job listings")
public class EmployerJobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal UserPrincipal employer) {
        JobResponse response = jobService.createJob(request, employer.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Job posted successfully", response));
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal UserPrincipal employer) {
        JobResponse response = jobService.updateJob(jobId, request, employer.getId());
        return ResponseEntity.ok(ApiResponse.success("Job updated", response));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal employer) {
        jobService.deleteJob(jobId, employer.getId());
        return ResponseEntity.ok(ApiResponse.success("Job deleted"));
    }

    @PatchMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<JobResponse>> toggleStatus(
            @PathVariable Long jobId,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal employer) {
        JobResponse response = jobService.toggleJobStatus(jobId, employer.getId(), status);
        return ResponseEntity.ok(ApiResponse.success("Job status updated", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs(
            @AuthenticationPrincipal UserPrincipal employer) {
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched", jobService.getJobsByEmployer(employer.getId())));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.success("Job fetched", jobService.getJobById(jobId)));
    }
}