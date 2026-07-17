package com.jobportal.repository;

import com.jobportal.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
}
