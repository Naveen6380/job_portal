package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationRequest {

    @NotBlank(message = "City is required")
    private String city;

    private String state;
    private String country = "India";
}
