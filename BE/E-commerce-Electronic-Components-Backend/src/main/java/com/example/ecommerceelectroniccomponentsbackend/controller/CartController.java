package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.CartDTO;
import com.example.ecommerceelectroniccomponentsbackend.dto.CartItemDTO;
import com.example.ecommerceelectroniccomponentsbackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cart = cartService.getCart(getCurrentUserId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody CartItemDTO cartItemDTO) {
        CartDTO updatedCart = cartService.addToCart(getCurrentUserId(), cartItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartDTO updatedCart = cartService.updateCartItem(getCurrentUserId(), cartItemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Long cartItemId) {
        CartDTO updatedCart = cartService.removeFromCart(getCurrentUserId(), cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
