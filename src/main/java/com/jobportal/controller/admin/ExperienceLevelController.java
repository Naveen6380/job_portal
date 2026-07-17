package com.jobportal.controller.admin;

import com.jobportal.dto.request.ExperienceLevelRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.ExperienceLevel;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ExperienceLevelRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/experience-levels")
@RequiredArgsConstructor
@Tag(name = "Admin - Experience Levels", description = "Manage experience level buckets (Fresher, 1-3 Years...)")
public class ExperienceLevelController {

    private final ExperienceLevelRepository experienceLevelRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceLevel>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Experience levels fetched", experienceLevelRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceLevel>> create(@Valid @RequestBody ExperienceLevelRequest request) {
        if (experienceLevelRepository.existsByLabelIgnoreCase(request.getLabel())) {
            throw new DuplicateResourceException("Experience level '" + request.getLabel() + "' already exists");
        }
        ExperienceLevel saved = experienceLevelRepository.save(ExperienceLevel.builder()
                .label(request.getLabel())
                .minYears(request.getMinYears())
                .maxYears(request.getMaxYears())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Experience level created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceLevel>> update(@PathVariable Long id, @Valid @RequestBody ExperienceLevelRequest request) {
        ExperienceLevel level = experienceLevelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience level not found with id: " + id));
        level.setLabel(request.getLabel());
        level.setMinYears(request.getMinYears());
        level.setMaxYears(request.getMaxYears());
        return ResponseEntity.ok(ApiResponse.success("Experience level updated", experienceLevelRepository.save(level)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!experienceLevelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experience level not found with id: " + id);
        }
        experienceLevelRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Experience level deleted"));
    }
}
