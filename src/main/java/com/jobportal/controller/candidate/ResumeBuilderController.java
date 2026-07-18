package com.jobportal.controller.candidate;

import com.jobportal.dto.request.EducationRequest;
import com.jobportal.dto.request.ExperienceRequest;
import com.jobportal.dto.request.ProjectRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;
import com.jobportal.entity.Project;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.ResumeBuilderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
@Tag(name = "Candidate - Resume Builder", description = "Manage Education, Experience, and Project sections of the resume")
public class ResumeBuilderController {

    private final ResumeBuilderService resumeBuilderService;

    // ---------- Education ----------

    @PostMapping("/education")
    public ResponseEntity<ApiResponse<Education>> addEducation(
            @Valid @RequestBody EducationRequest request,
            @AuthenticationPrincipal UserPrincipal candidate) {
        Education saved = resumeBuilderService.addEducation(candidate.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Education added", saved));
    }

    @GetMapping("/education")
    public ResponseEntity<ApiResponse<List<Education>>> getEducation(
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Education fetched", resumeBuilderService.getEducation(candidate.getId())));
    }

    @DeleteMapping("/education/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal candidate) {
        resumeBuilderService.deleteEducation(candidate.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    // ---------- Experience ----------

    @PostMapping("/experience")
    public ResponseEntity<ApiResponse<Experience>> addExperience(
            @Valid @RequestBody ExperienceRequest request,
            @AuthenticationPrincipal UserPrincipal candidate) {
        Experience saved = resumeBuilderService.addExperience(candidate.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Experience added", saved));
    }

    @GetMapping("/experience")
    public ResponseEntity<ApiResponse<List<Experience>>> getExperience(
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Experience fetched", resumeBuilderService.getExperience(candidate.getId())));
    }

    @DeleteMapping("/experience/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal candidate) {
        resumeBuilderService.deleteExperience(candidate.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted"));
    }

    // ---------- Projects ----------

    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<Project>> addProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserPrincipal candidate) {
        Project saved = resumeBuilderService.addProject(candidate.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Project added", saved));
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<Project>>> getProjects(
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Projects fetched", resumeBuilderService.getProjects(candidate.getId())));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal candidate) {
        resumeBuilderService.deleteProject(candidate.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted"));
    }
}