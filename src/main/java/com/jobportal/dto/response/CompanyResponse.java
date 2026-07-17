package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String logoUrl;
    private String website;
    private String industry;
    private String about;
    private String contactEmail;
    private String verificationStatus;
    private LocalDateTime createdAt;
}
