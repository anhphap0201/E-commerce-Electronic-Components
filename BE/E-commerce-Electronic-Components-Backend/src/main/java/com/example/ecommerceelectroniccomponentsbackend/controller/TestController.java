package com.example.ecommerceelectroniccomponentsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Test Endpoints", description = "Demo endpoints for testing role-based authorization")
public class TestController {

    @Operation(
            summary = "Public endpoint",
            description = "This endpoint is accessible to everyone without authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint - accessible to everyone");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User profile",
            description = "Accessible to users with USER or ADMIN role",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content)
    })
    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to user profile");
        response.put("email", authentication != null ? authentication.getName() : "Unknown");
        response.put("role", authentication != null ? authentication.getAuthorities().toString() : "No role");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Admin dashboard",
            description = "Accessible only to users with ADMIN role",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content)
    })
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> adminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to admin dashboard");
        response.put("email", authentication != null ? authentication.getName() : "Unknown");
        response.put("role", authentication != null ? authentication.getAuthorities().toString() : "No role");
        return ResponseEntity.ok(response);
    }
}
