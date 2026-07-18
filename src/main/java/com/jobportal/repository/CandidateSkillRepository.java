package com.jobportal.repository;

import com.jobportal.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {
    List<CandidateSkill> findByCandidateId(Long candidateId);
    boolean existsByCandidateIdAndSkillId(Long candidateId, Long skillId);
    void deleteByCandidateIdAndSkillId(Long candidateId, Long skillId);
}