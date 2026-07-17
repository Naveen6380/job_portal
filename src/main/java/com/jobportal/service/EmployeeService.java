package com.jobportal.service;

import com.jobportal.dto.request.AssignPermissionRequest;
import com.jobportal.dto.request.CreateEmployeeRequest;
import com.jobportal.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(CreateEmployeeRequest request);
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse assignPermission(Long employeeId, AssignPermissionRequest request);
    void revokePermission(Long employeeId, String permissionKey);
    void deactivateEmployee(Long employeeId);
}
