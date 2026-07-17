package com.jobportal.repository;

import com.jobportal.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceLevelRepository extends JpaRepository<ExperienceLevel, Long> {
    boolean existsByLabelIgnoreCase(String label);
}
