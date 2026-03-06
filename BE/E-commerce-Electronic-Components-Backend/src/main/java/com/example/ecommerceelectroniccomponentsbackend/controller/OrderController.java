package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.OrderDTO;
import com.example.ecommerceelectroniccomponentsbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder() {
        OrderDTO order = orderService.createOrderFromCart(getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrderHistory(getCurrentUserId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId, getCurrentUserId());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, getCurrentUserId(), status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        OrderDTO cancelledOrder = orderService.cancelOrder(orderId, getCurrentUserId());
        return ResponseEntity.ok(cancelledOrder);
    }
}
