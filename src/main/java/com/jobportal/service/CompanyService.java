package com.jobportal.service;

import com.jobportal.dto.response.CompanyResponse;

import java.util.List;

public interface CompanyService {
    List<CompanyResponse> getCompaniesByStatus(String status);
    CompanyResponse approveCompany(Long companyId, Long adminUserId);
    CompanyResponse rejectCompany(Long companyId, Long adminUserId, String reason);
}
