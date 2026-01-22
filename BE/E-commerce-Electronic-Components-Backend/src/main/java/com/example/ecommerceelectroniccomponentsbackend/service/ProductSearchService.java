package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.CategoryDTO;
import com.example.ecommerceelectroniccomponentsbackend.dto.ProductVariantDTO;
import com.example.ecommerceelectroniccomponentsbackend.dto.ProductWithVariantsDTO;
import com.example.ecommerceelectroniccomponentsbackend.entity.Category;
import com.example.ecommerceelectroniccomponentsbackend.entity.Product;
import com.example.ecommerceelectroniccomponentsbackend.entity.ProductVariant;
import com.example.ecommerceelectroniccomponentsbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductWithVariantsDTO> searchProductsByCategoryName(String categoryName) {
        log.info("Searching products by category name: {}", categoryName);
        List<Product> products = productRepository.findByCategoryNameContaining(categoryName);
        log.info("Found {} products for category name: {}", products.size(), categoryName);
        return products.stream()
                .map(this::convertToProductWithVariantsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductWithVariantsDTO> searchProductsByCategoryId(Long categoryId) {
        log.info("Searching products by category ID: {}", categoryId);
        List<Product> products = productRepository.findByCategoryId(categoryId);
        log.info("Found {} products for category ID: {}", products.size(), categoryId);
        return products.stream()
                .map(this::convertToProductWithVariantsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductWithVariantsDTO> searchProductsByCategorySlug(String categorySlug) {
        log.info("Searching products by category slug: {}", categorySlug);
        List<Product> products = productRepository.findByCategorySlug(categorySlug);
        log.info("Found {} products for category slug: {}", products.size(), categorySlug);

        // Nếu không tìm thấy với exact match, thử partial match
        if (products.isEmpty()) {
            log.info("No exact match found. Trying partial match for slug: {}", categorySlug);
            products = productRepository.findByCategorySlugContaining(categorySlug);
            log.info("Found {} products with partial slug match", products.size());
        }

        return products.stream()
                .map(this::convertToProductWithVariantsDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductWithVariantsDTO> searchProductsByName(String productName) {
        log.info("Searching products by name: {}", productName);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);
        log.info("Found {} products for name: {}", products.size(), productName);
        return products.stream()
                .map(this::convertToProductWithVariantsDTO)
                .collect(Collectors.toList());
    }


    public ProductWithVariantsDTO convertToProductWithVariantsDTO(Product product) {
        ProductWithVariantsDTO dto = new ProductWithVariantsDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setShortDescription(product.getShortDescription());
        dto.setDescription(product.getDescription());
        dto.setAvgRating(product.getAvgRating());
        dto.setSoldQuantity(product.getSoldQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Convert categories
        List<CategoryDTO> categoryDTOs = product.getCategories().stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
        dto.setCategories(categoryDTOs);

        // Convert variants
        List<ProductVariantDTO> variantDTOs = product.getVariants().stream()
                .map(this::convertToVariantDTO)
                .collect(Collectors.toList());
        dto.setVariants(variantDTOs);

        return dto;
    }


    private CategoryDTO convertToCategoryDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }


    private ProductVariantDTO convertToVariantDTO(ProductVariant variant) {
        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setId(variant.getId());
        dto.setProductId(variant.getProduct().getId());
        dto.setProductName(variant.getProduct().getName());
        dto.setVariantName(variant.getVariantName());
        dto.setPrice(variant.getPrice());
        dto.setDiscountPrice(variant.getDiscountPrice());
        dto.setInStock(variant.getInStock());
        dto.setIsAvailable(variant.getIsAvailable());
        dto.setCreatedAt(variant.getCreatedAt());
        dto.setUpdatedAt(variant.getUpdatedAt());
        return dto;
    }
}
