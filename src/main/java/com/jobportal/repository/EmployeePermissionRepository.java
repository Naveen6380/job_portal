package com.jobportal.repository;

import com.jobportal.entity.EmployeePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeePermissionRepository extends JpaRepository<EmployeePermission, Long> {
    List<EmployeePermission> findByEmployeeId(Long employeeId);
    void deleteByEmployeeIdAndPermissionKey(Long employeeId, String permissionKey);
}
