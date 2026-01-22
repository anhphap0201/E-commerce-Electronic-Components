package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.ProductWithVariantsDTO;
import com.example.ecommerceelectroniccomponentsbackend.service.ProductSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/search")
@RequiredArgsConstructor
@Tag(name = "Product Search", description = "API tìm kiếm sản phẩm")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/by-category-name")
    @Operation(summary = "Tìm kiếm sản phẩm theo tên danh mục",
               description = "Tìm tất cả sản phẩm (kèm variants) thuộc danh mục có tên chứa từ khóa tìm kiếm")
    public ResponseEntity<List<ProductWithVariantsDTO>> searchByCategoryName(
            @Parameter(description = "Tên danh mục cần tìm", example = "Arduino")
            @RequestParam String categoryName) {
        List<ProductWithVariantsDTO> products = productSearchService.searchProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-category-id/{categoryId}")
    @Operation(summary = "Tìm kiếm sản phẩm theo ID danh mục",
               description = "Tìm tất cả sản phẩm (kèm variants) thuộc danh mục theo ID")
    public ResponseEntity<List<ProductWithVariantsDTO>> searchByCategoryId(
            @Parameter(description = "ID của danh mục", example = "1")
            @PathVariable Long categoryId) {
        List<ProductWithVariantsDTO> products = productSearchService.searchProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-category-slug")
    @Operation(summary = "Tìm kiếm sản phẩm theo slug danh mục",
               description = "Tìm tất cả sản phẩm (kèm variants) thuộc danh mục theo slug")
    public ResponseEntity<List<ProductWithVariantsDTO>> searchByCategorySlug(
            @Parameter(description = "Slug của danh mục", example = "arduino")
            @RequestParam String categorySlug) {
        List<ProductWithVariantsDTO> products = productSearchService.searchProductsByCategorySlug(categorySlug);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-product-name")
    @Operation(summary = "Tìm kiếm sản phẩm theo tên sản phẩm",
               description = "Tìm sản phẩm (kèm variants) có tên chứa từ khóa tìm kiếm")
    public ResponseEntity<List<ProductWithVariantsDTO>> searchByProductName(
            @Parameter(description = "Tên sản phẩm cần tìm", example = "Arduino Uno")
            @RequestParam String productName) {
        List<ProductWithVariantsDTO> products = productSearchService.searchProductsByName(productName);
        return ResponseEntity.ok(products);
    }
}
