package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignPermissionRequest {

    @NotBlank(message = "Permission key is required")
    private String permissionKey;   // MANAGE_JOBS, MANAGE_CANDIDATES, SEND_EMAIL, SEND_SMS, VIEW_REPORTS
}
