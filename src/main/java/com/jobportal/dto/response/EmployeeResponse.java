package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private LocalDate joinedDate;
    private String reportingToName;
    private List<String> permissions;
}
