package com.jobportal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "CANDIDATE|EMPLOYER", message = "Self-registration only allowed for CANDIDATE or EMPLOYER")
    private String role;   // ADMIN/EMPLOYEE accounts are created only by Admin, never self-registered

    // Required only when role = EMPLOYER
    private String companyName;
}
