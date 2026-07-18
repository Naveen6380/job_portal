package com.jobportal.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateProfileRequest {
    private String headline;
    private String summary;
    private Long locationId;
    private Long qualificationId;
    private Long experienceLevelId;
    private BigDecimal currentCtc;
    private BigDecimal expectedCtc;
    private Integer noticePeriodDays;
    private Boolean isOpenToRemote;
}