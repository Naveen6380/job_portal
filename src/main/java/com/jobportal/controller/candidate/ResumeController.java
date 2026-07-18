package com.jobportal.controller.candidate;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.CandidateProfile;
import com.jobportal.entity.ResumeFile;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CandidateProfileRepository;
import com.jobportal.repository.ResumeFileRepository;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/candidate/resumes")
@RequiredArgsConstructor
@Tag(name = "Candidate - Resume Upload", description = "Upload, list, and manage resume files (PDF/DOCX via Cloudinary)")
public class ResumeController {

    private final FileStorageService fileStorageService;
    private final ResumeFileRepository resumeFileRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @PostMapping(consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<ApiResponse<ResumeFile>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal candidate) {

        CandidateProfile profile = candidateProfileRepository.findByUserId(candidate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Please create your profile before uploading a resume"));

        String fileUrl = fileStorageService.uploadFile(file, "resumes");

        // Unset any existing primary resume - only one primary allowed at a time
        resumeFileRepository.findByCandidateIdAndIsPrimaryTrue(profile.getId())
                .forEach(r -> { r.setIsPrimary(false); resumeFileRepository.save(r); });

        String fileType = file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".pdf")
                ? "PDF" : "DOCX";

        ResumeFile resume = ResumeFile.builder()
                .candidate(profile)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(fileType)
                .isPrimary(true)
                .build();

        resume = resumeFileRepository.save(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Resume uploaded", resume));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeFile>>> getMyResumes(@AuthenticationPrincipal UserPrincipal candidate) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return ResponseEntity.ok(ApiResponse.success("Resumes fetched", resumeFileRepository.findByCandidateId(profile.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal candidate) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        ResumeFile resume = resumeFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        if (!resume.getCandidate().getId().equals(profile.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not own this resume");
        }
        resumeFileRepository.delete(resume);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted"));
    }
}