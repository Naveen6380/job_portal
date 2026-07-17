package com.jobportal.service.impl;

import com.jobportal.dto.response.CompanyResponse;
import com.jobportal.entity.Company;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<CompanyResponse> getCompaniesByStatus(String status) {
        List<Company> companies = (status == null || status.isBlank())
                ? companyRepository.findAll()
                : companyRepository.findByVerificationStatus(status.toUpperCase());
        return companies.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyResponse approveCompany(Long companyId, Long adminUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        company.setVerificationStatus("APPROVED");
        company.setVerifiedBy(adminUserId);
        company.setVerifiedAt(LocalDateTime.now());
        // TODO Day 4: EmailService.send(company approved notification)
        return toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse rejectCompany(Long companyId, Long adminUserId, String reason) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        company.setVerificationStatus("REJECTED");
        company.setVerifiedBy(adminUserId);
        company.setVerifiedAt(LocalDateTime.now());
        // TODO Day 4: EmailService.send(company rejected notification, reason)
        return toResponse(companyRepository.save(company));
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .logoUrl(company.getLogoUrl())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .about(company.getAbout())
                .contactEmail(company.getUser().getEmail())
                .verificationStatus(company.getVerificationStatus())
                .createdAt(company.getCreatedAt())
                .build();
    }
}
