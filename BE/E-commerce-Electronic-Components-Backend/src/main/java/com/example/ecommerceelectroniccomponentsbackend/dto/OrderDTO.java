package com.example.ecommerceelectroniccomponentsbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Builder.Default
    private String status = "PENDING";

    @NotNull(message = "Total price cannot be null")
    private BigDecimal totalPrice;

    @Builder.Default
    private Set<OrderItemDTO> orderItems = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
