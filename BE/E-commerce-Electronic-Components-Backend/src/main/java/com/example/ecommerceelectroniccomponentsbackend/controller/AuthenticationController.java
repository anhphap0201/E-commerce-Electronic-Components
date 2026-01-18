package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.request.ChangePasswordRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.LoginRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.RegisterRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.ChangePasswordResponse;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.LoginResponse;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.RegisterResponse;
import com.example.ecommerceelectroniccomponentsbackend.service.AuthenticationService;
import com.example.ecommerceelectroniccomponentsbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and Authorization APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(
            summary = "Register new user",
            description = "Create a new user account with email, password and role (USER or ADMIN)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
                    content = @Content)
    })
    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Login",
            description = "Authenticate user and receive JWT access token and refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from(
                        "refresh_token",
                        loginResponse.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false) // true nếu HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Logout",
            description = "Logout user and blacklist the access token",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content)
    })
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Refresh token from cookie")
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) throws ParseException {

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = authHeader.substring(7);
        authenticationService.logout(accessToken, refreshToken);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Change password",
            description = "Change password for authenticated user. Current token will be invalidated and user must login again with new password.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully. User must login again.",
                    content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password or passwords don't match",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content)
    })
    @PostMapping("/auth/change-password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = userService.changePassword(request);
        return ResponseEntity.ok(response);
    }

}
