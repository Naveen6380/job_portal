package com.jobportal.service.impl;

import com.jobportal.dto.request.AssignPermissionRequest;
import com.jobportal.dto.request.CreateEmployeeRequest;
import com.jobportal.dto.response.EmployeeResponse;
import com.jobportal.entity.Employee;
import com.jobportal.entity.EmployeePermission;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.EmployeePermissionRepository;
import com.jobportal.repository.EmployeeRepository;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeePermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new IllegalStateException("ROLE_EMPLOYEE missing - check schema seed data"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(employeeRole)
                .isActive(true)
                .isEmailVerified(true)   // admin-created accounts are pre-verified
                .build();
        user = userRepository.save(user);

        Employee reportingTo = null;
        if (request.getReportingToEmployeeId() != null) {
            reportingTo = employeeRepository.findById(request.getReportingToEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager (employee) not found with id: " + request.getReportingToEmployeeId()));
        }

        Employee employee = Employee.builder()
                .user(user)
                .employeeCode(generateEmployeeCode(user.getId()))
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joinedDate(request.getJoinedDate())
                .reportingTo(reportingTo)
                .build();
        employee = employeeRepository.save(employee);

        // TODO Day 4: send welcome email with credentials via EmailService

        return toResponse(employee, List.of());
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(emp -> toResponse(emp, getPermissionKeys(emp.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return toResponse(employee, getPermissionKeys(id));
    }

    @Override
    @Transactional
    public EmployeeResponse assignPermission(Long employeeId, AssignPermissionRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        boolean alreadyGranted = permissionRepository.findByEmployeeId(employeeId).stream()
                .anyMatch(p -> p.getPermissionKey().equalsIgnoreCase(request.getPermissionKey()));

        if (!alreadyGranted) {
            permissionRepository.save(EmployeePermission.builder()
                    .employee(employee)
                    .permissionKey(request.getPermissionKey().toUpperCase())
                    .build());
        }
        return toResponse(employee, getPermissionKeys(employeeId));
    }

    @Override
    @Transactional
    public void revokePermission(Long employeeId, String permissionKey) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        permissionRepository.deleteByEmployeeIdAndPermissionKey(employeeId, permissionKey.toUpperCase());
    }

    @Override
    @Transactional
    public void deactivateEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        User user = employee.getUser();
        user.setIsActive(false);
        userRepository.save(user);
    }

    private List<String> getPermissionKeys(Long employeeId) {
        return permissionRepository.findByEmployeeId(employeeId).stream()
                .map(EmployeePermission::getPermissionKey)
                .collect(Collectors.toList());
    }

    private String generateEmployeeCode(Long userId) {
        return "EMP" + String.format("%04d", userId);
    }

    private EmployeeResponse toResponse(Employee employee, List<String> permissions) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(employee.getUser().getFullName())
                .email(employee.getUser().getEmail())
                .phone(employee.getUser().getPhone())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .joinedDate(employee.getJoinedDate())
                .reportingToName(employee.getReportingTo() != null
                        ? employee.getReportingTo().getUser().getFullName() : null)
                .permissions(permissions)
                .build();
    }
}
