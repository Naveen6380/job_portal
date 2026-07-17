package com.jobportal.controller.admin;

import com.jobportal.dto.request.LocationRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Location;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.LocationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Tag(name = "Admin - Locations", description = "Manage city/state master list for job postings")
public class LocationController {

    private final LocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Locations fetched", locationRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> create(@Valid @RequestBody LocationRequest request) {
        if (locationRepository.existsByCityIgnoreCaseAndStateIgnoreCase(request.getCity(), request.getState())) {
            throw new DuplicateResourceException("Location already exists: " + request.getCity() + ", " + request.getState());
        }
        Location saved = locationRepository.save(Location.builder()
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .isActive(true)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Location created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Location>> update(@PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setCountry(request.getCountry());
        return ResponseEntity.ok(ApiResponse.success("Location updated", locationRepository.save(location)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Location deleted"));
    }
}
