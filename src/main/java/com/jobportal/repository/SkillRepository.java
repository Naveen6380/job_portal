package com.jobportal.repository;

import com.jobportal.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Skill> findByNameContainingIgnoreCase(String query);
}
