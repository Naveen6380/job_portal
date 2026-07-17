package com.jobportal.repository;

import com.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByJobCompanyId(Long companyId);
    List<Application> findByCandidateId(Long candidateId);
    Optional<Application> findByJobIdAndCandidateId(Long jobId, Long candidateId);
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
}