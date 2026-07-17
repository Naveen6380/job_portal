package com.jobportal.repository;

import com.jobportal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserId(Long userId);
    List<Company> findByVerificationStatus(String status);
}
