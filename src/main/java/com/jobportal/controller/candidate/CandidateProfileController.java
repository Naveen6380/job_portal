package com.jobportal.controller.candidate;

import com.jobportal.dto.request.CandidateProfileRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.CandidateProfileResponse;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.CandidateProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate/profile")
@RequiredArgsConstructor
@Tag(name = "Candidate - Profile", description = "Create/update candidate profile and manage skills")
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", profileService.getMyProfile(candidate.getId())));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateProfile(
            @RequestBody CandidateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profileService.updateProfile(candidate.getId(), request)));
    }

    @PostMapping("/skills/{skillId}")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> addSkill(
            @PathVariable Long skillId,
            @RequestParam(required = false) String proficiency,
            @AuthenticationPrincipal UserPrincipal candidate) {
        return ResponseEntity.ok(ApiResponse.success("Skill added", profileService.addSkill(candidate.getId(), skillId, proficiency)));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<ApiResponse<Void>> removeSkill(
            @PathVariable Long skillId,
            @AuthenticationPrincipal UserPrincipal candidate) {
        profileService.removeSkill(candidate.getId(), skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill removed"));
    }
}