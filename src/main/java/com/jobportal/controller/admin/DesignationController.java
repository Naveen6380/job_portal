package com.jobportal.controller.admin;

import com.jobportal.dto.request.DesignationRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Category;
import com.jobportal.entity.Designation;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CategoryRepository;
import com.jobportal.repository.DesignationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/designations")
@RequiredArgsConstructor
@Tag(name = "Admin - Designations", description = "Manage job designation master list (Software Engineer, Data Analyst...)")
public class DesignationController {

    private final DesignationRepository designationRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Designation>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Designations fetched", designationRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Designation>> create(@Valid @RequestBody DesignationRequest request) {
        if (designationRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new DuplicateResourceException("Designation '" + request.getTitle() + "' already exists");
        }
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }
        Designation saved = designationRepository.save(Designation.builder()
                .title(request.getTitle())
                .category(category)
                .isActive(true)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Designation created", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!designationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Designation not found with id: " + id);
        }
        designationRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Designation deleted"));
    }
}
