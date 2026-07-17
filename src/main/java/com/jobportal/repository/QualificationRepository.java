package com.jobportal.repository;

import com.jobportal.entity.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    boolean existsByNameIgnoreCase(String name);
}
