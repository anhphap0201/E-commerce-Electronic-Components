package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.dto.request.LoginRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.LoginResponse;
import com.example.ecommerceelectroniccomponentsbackend.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

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

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
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

}
