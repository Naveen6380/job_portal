package com.jobportal.controller.admin;

import com.jobportal.dto.request.NameRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Category;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Categories", description = "Manage job categories (IT, Finance, Marketing...)")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(@Valid @RequestBody NameRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }
        Category saved = categoryRepository.save(Category.builder().name(request.getName()).isActive(true).build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> update(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.getName());
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryRepository.save(category)));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Category>> toggleStatus(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setIsActive(!category.getIsActive());
        return ResponseEntity.ok(ApiResponse.success("Category status updated", categoryRepository.save(category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}
