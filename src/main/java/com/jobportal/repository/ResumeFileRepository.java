package com.jobportal.repository;

import com.jobportal.entity.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {
    List<ResumeFile> findByCandidateId(Long candidateId);
    List<ResumeFile> findByCandidateIdAndIsPrimaryTrue(Long candidateId);
}