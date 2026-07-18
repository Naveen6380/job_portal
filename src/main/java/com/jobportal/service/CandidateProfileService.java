package com.jobportal.service;

import com.jobportal.dto.request.CandidateProfileRequest;
import com.jobportal.dto.response.CandidateProfileResponse;

public interface CandidateProfileService {
    CandidateProfileResponse getMyProfile(Long userId);
    CandidateProfileResponse updateProfile(Long userId, CandidateProfileRequest request);
    CandidateProfileResponse addSkill(Long userId, Long skillId, String proficiency);
    void removeSkill(Long userId, Long skillId);
}