package com.jobportal.controller.admin;

import com.jobportal.dto.request.AssignPermissionRequest;
import com.jobportal.dto.request.CreateEmployeeRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.EmployeeResponse;
import com.jobportal.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
@Tag(name = "Admin - Employees", description = "Create internal staff accounts and manage their module permissions")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Employees fetched", employeeService.getAllEmployees()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee fetched", employeeService.getEmployeeById(id)));
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<EmployeeResponse>> assignPermission(
            @PathVariable Long id, @Valid @RequestBody AssignPermissionRequest request) {
        EmployeeResponse response = employeeService.assignPermission(id, request);
        return ResponseEntity.ok(ApiResponse.success("Permission assigned", response));
    }

    @DeleteMapping("/{id}/permissions/{permissionKey}")
    public ResponseEntity<ApiResponse<Void>> revokePermission(
            @PathVariable Long id, @PathVariable String permissionKey) {
        employeeService.revokePermission(id, permissionKey);
        return ResponseEntity.ok(ApiResponse.success("Permission revoked"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated"));
    }
}
