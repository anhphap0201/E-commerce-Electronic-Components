package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.entity.Category;
import com.example.ecommerceelectroniccomponentsbackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Create
    public Category createCategory(Category category) {
        category.setCreatedAt(System.currentTimeMillis());
        category.setUpdatedAt(System.currentTimeMillis());
        return categoryRepository.save(category);
    }
    
    // Read all
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // Read by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    // Read by name
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    // Update
    public Optional<Category> updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            category.setUpdatedAt(System.currentTimeMillis());
            return categoryRepository.save(category);
        });
    }
    
    // Delete
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Check if category exists by name
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
