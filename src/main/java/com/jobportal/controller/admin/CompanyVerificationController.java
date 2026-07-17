package com.jobportal.controller.admin;

import com.jobportal.dto.request.RejectCompanyRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.CompanyResponse;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
@Tag(name = "Admin - Company Verification", description = "Approve or reject employer company registrations")
public class CompanyVerificationController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getCompanies(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(
                ApiResponse.success("Companies fetched", companyService.getCompaniesByStatus(status)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<CompanyResponse>> approve(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal admin) {
        CompanyResponse response = companyService.approveCompany(id, admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Company approved", response));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<CompanyResponse>> reject(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal admin,
            @RequestBody(required = false) RejectCompanyRequest request) {
        String reason = request != null ? request.getReason() : null;
        CompanyResponse response = companyService.rejectCompany(id, admin.getId(), reason);
        return ResponseEntity.ok(ApiResponse.success("Company rejected", response));
    }
}
