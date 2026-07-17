package com.jobportal.repository;

import com.jobportal.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
