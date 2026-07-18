package com.jobportal.service.impl;

import com.jobportal.dto.request.CandidateProfileRequest;
import com.jobportal.dto.response.CandidateProfileResponse;
import com.jobportal.entity.*;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.*;
import com.jobportal.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final ResumeFileRepository resumeFileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final LocationRepository locationRepository;
    private final QualificationRepository qualificationRepository;
    private final ExperienceLevelRepository experienceLevelRepository;

    @Override
    @Transactional
    public CandidateProfileResponse getMyProfile(Long userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(Long userId, CandidateProfileRequest request) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));

        profile.setHeadline(request.getHeadline());
        profile.setSummary(request.getSummary());
        profile.setCurrentCtc(request.getCurrentCtc());
        profile.setExpectedCtc(request.getExpectedCtc());
        profile.setNoticePeriodDays(request.getNoticePeriodDays());
        profile.setIsOpenToRemote(request.getIsOpenToRemote() != null ? request.getIsOpenToRemote() : false);

        if (request.getLocationId() != null) {
            profile.setLocation(locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
        }
        if (request.getQualificationId() != null) {
            profile.setQualification(qualificationRepository.findById(request.getQualificationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Qualification not found")));
        }
        if (request.getExperienceLevelId() != null) {
            profile.setExperienceLevel(experienceLevelRepository.findById(request.getExperienceLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Experience level not found")));
        }

        profile.setProfileCompletionPct(calculateCompletion(profile));
        profile = candidateProfileRepository.save(profile);

        return toResponse(profile);
    }

    @Override
    @Transactional
    public CandidateProfileResponse addSkill(Long userId, Long skillId, String proficiency) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));

        if (candidateSkillRepository.existsByCandidateIdAndSkillId(profile.getId(), skillId)) {
            throw new DuplicateResourceException("Skill already added to your profile");
        }

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

        candidateSkillRepository.save(CandidateSkill.builder()
                .candidate(profile)
                .skill(skill)
                .proficiency(proficiency != null ? proficiency : "INTERMEDIATE")
                .build());

        profile.setProfileCompletionPct(calculateCompletion(profile));
        candidateProfileRepository.save(profile);

        return toResponse(profile);
    }

    @Override
    @Transactional
    public void removeSkill(Long userId, Long skillId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        candidateSkillRepository.deleteByCandidateIdAndSkillId(profile.getId(), skillId);
    }

    // ---------- helpers ----------

    private CandidateProfile createEmptyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return candidateProfileRepository.save(CandidateProfile.builder().user(user).build());
    }

    /**
     * Simple weighted completion score - each filled section adds points.
     * This is what powers the "Profile Completion %" widget on the candidate dashboard.
     */
    private Integer calculateCompletion(CandidateProfile profile) {
        int score = 0;
        if (profile.getHeadline() != null && !profile.getHeadline().isBlank()) score += 15;
        if (profile.getSummary() != null && !profile.getSummary().isBlank()) score += 15;
        if (profile.getLocation() != null) score += 10;
        if (profile.getQualification() != null) score += 10;
        if (profile.getExperienceLevel() != null) score += 10;
        if (profile.getExpectedCtc() != null) score += 10;
        if (!candidateSkillRepository.findByCandidateId(profile.getId()).isEmpty()) score += 15;
        if (!resumeFileRepository.findByCandidateId(profile.getId()).isEmpty()) score += 15;
        return Math.min(score, 100);
    }

    private CandidateProfileResponse toResponse(CandidateProfile profile) {
        List<String> skills = candidateSkillRepository.findByCandidateId(profile.getId()).stream()
                .map(cs -> cs.getSkill().getName())
                .collect(Collectors.toList());

        String primaryResumeUrl = resumeFileRepository.findByCandidateIdAndIsPrimaryTrue(profile.getId())
                .stream().findFirst().map(ResumeFile::getFileUrl).orElse(null);

        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getUser().getPhone())
                .headline(profile.getHeadline())
                .summary(profile.getSummary())
                .location(profile.getLocation() != null ? profile.getLocation().getCity() : null)
                .qualification(profile.getQualification() != null ? profile.getQualification().getName() : null)
                .experienceLevel(profile.getExperienceLevel() != null ? profile.getExperienceLevel().getLabel() : null)
                .currentCtc(profile.getCurrentCtc())
                .expectedCtc(profile.getExpectedCtc())
                .noticePeriodDays(profile.getNoticePeriodDays())
                .profileCompletionPct(profile.getProfileCompletionPct())
                .resumeScore(profile.getResumeScore())
                .isOpenToRemote(profile.getIsOpenToRemote())
                .skills(skills)
                .primaryResumeUrl(primaryResumeUrl)
                .build();
    }
}