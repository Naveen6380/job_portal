package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String headline;
    private String summary;
    private String location;
    private String qualification;
    private String experienceLevel;
    private BigDecimal currentCtc;
    private BigDecimal expectedCtc;
    private Integer noticePeriodDays;
    private Integer profileCompletionPct;
    private Integer resumeScore;
    private Boolean isOpenToRemote;
    private List<String> skills;
    private String primaryResumeUrl;
}