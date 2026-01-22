package com.example.ecommerceelectroniccomponentsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithVariantsDTO {
    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private Double avgRating;
    private Integer soldQuantity;
    private List<CategoryDTO> categories = new ArrayList<>();
    private List<ProductVariantDTO> variants = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
