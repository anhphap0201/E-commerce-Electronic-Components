package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.request.ChangePasswordRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.RegisterRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.UserCreateRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.UserProfileRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.ChangePasswordResponse;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.RegisterResponse;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.UserCreateResponse;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.UserProfileResponse;
import com.example.ecommerceelectroniccomponentsbackend.model.BlacklistedToken;
import com.example.ecommerceelectroniccomponentsbackend.model.Role;
import com.example.ecommerceelectroniccomponentsbackend.model.User;
import com.example.ecommerceelectroniccomponentsbackend.repository.BlacklistedTokenRepository;
import com.example.ecommerceelectroniccomponentsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public UserCreateResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        return UserCreateResponse.builder()
                .email(request.getEmail())
                .build();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole() : Role.ROLE_USER)
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Verify new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Blacklist current token to force re-login with new password
        try {
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                String jwtId = jwt.getClaimAsString("jti");
                Date expirationTime = Date.from(jwt.getExpiresAt());

                if (jwtId != null && expirationTime != null) {
                    long ttl = expirationTime.getTime() - System.currentTimeMillis();

                    if (ttl > 0) {
                        blacklistedTokenRepository.save(
                                BlacklistedToken.builder()
                                        .jwtId(jwtId)
                                        .ttl(ttl)
                                        .build()
                        );
                    }
                }
            }
        } catch (Exception e) {
            // Log but don't fail the password change
            System.err.println("Warning: Could not blacklist token after password change: " + e.getMessage());
        }

        return ChangePasswordResponse.builder()
                .message("Password changed successfully. Please login again with your new password.")
                .email(user.getEmail())
                .build();
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToUserProfileResponse(user);
    }

    public UserProfileResponse upsertCurrentUserProfile(UserProfileRequest request) {
        User user = getCurrentUser();

        if (!Objects.equals(user.getEmail(), request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setProvince(request.getProvince());
        user.setWard(request.getWard());
        user.setDetailedAddress(request.getDetailedAddress());

        userRepository.save(user);
        return mapToUserProfileResponse(user);
    }

    public UserProfileResponse clearCurrentUserProfile() {
        User user = getCurrentUser();
        user.setFullName(null);
        user.setPhone(null);
        user.setProvince(null);
        user.setWard(null);
        user.setDetailedAddress(null);
        userRepository.save(user);
        return mapToUserProfileResponse(user);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .province(user.getProvince())
                .ward(user.getWard())
                .detailedAddress(user.getDetailedAddress())
                .role(user.getRole())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }

        String principal = authentication.getName();

        try {
            Long userId = Long.parseLong(principal);
            return userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } catch (NumberFormatException ignored) {
            return userRepository.findByEmail(principal)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
    }
}

