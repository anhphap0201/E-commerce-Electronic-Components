package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.entity.Category;
import com.example.ecommerceelectroniccomponentsbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // CREATE - POST /api/categories
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            if (categoryService.existsByName(category.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Danh mục với tên này đã tồn tại");
            }
            Category created = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi tạo danh mục: " + e.getMessage());
        }
    }

    // READ ALL - GET /api/categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // READ BY ID - GET /api/categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Danh mục với ID " + id + " không tồn tại");
    }

    // READ BY NAME - GET /api/categories/search/{name}
    @GetMapping("/search/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        Optional<Category> category = categoryService.getCategoryByName(name);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Danh mục với tên '" + name + "' không tồn tại");
    }

    // UPDATE - PUT /api/categories/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        try {
            Optional<Category> updated = categoryService.updateCategory(id, updatedCategory);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Danh mục với ID " + id + " không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
    }

    // DELETE - DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            if (categoryService.deleteCategory(id)) {
                return ResponseEntity.ok("Danh mục đã được xóa thành công");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Danh mục với ID " + id + " không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi xóa danh mục: " + e.getMessage());
        }
    }
}
