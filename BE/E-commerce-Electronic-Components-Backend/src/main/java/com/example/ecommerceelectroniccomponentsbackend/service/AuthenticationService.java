package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.JwtInfo;
import com.example.ecommerceelectroniccomponentsbackend.dto.TokenPayload;
import com.example.ecommerceelectroniccomponentsbackend.dto.request.LoginRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.response.LoginResponse;
import com.example.ecommerceelectroniccomponentsbackend.model.BlacklistedToken;
import com.example.ecommerceelectroniccomponentsbackend.model.RefreshToken;
import com.example.ecommerceelectroniccomponentsbackend.model.User;
import com.example.ecommerceelectroniccomponentsbackend.repository.BlacklistedTokenRepository;
import com.example.ecommerceelectroniccomponentsbackend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authenticate = authenticationManager.authenticate(token);

        // Trả về token
        User user = (User) authenticate.getPrincipal();
        TokenPayload accessPayload = jwtService.generateAccessToken(user);
        TokenPayload refreshPayload = jwtService.generateRefreshToken(user);

        long refreshTtl =
                refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();

        refreshTokenRepository.save(RefreshToken.builder()
                .jwtId(refreshPayload.getJwtId())
                .ttl(refreshTtl)
                .build()
        );


        return LoginResponse.builder()
                .accessToken(accessPayload.getToken())
                .refreshToken(refreshPayload.getToken())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public void logout(String accessToken, String refreshToken) throws ParseException {
        JwtInfo accessInfo = jwtService.parseToken(accessToken);

        long ttl =
                accessInfo.getExpirationTime().getTime() - System.currentTimeMillis();

        if (ttl > 0) {
            blacklistedTokenRepository.save(
                    BlacklistedToken.builder()
                            .jwtId(accessInfo.getJwtId())
                            .ttl(ttl)
                            .build()
            );
        }

        JwtInfo refreshInfo = jwtService.parseToken(refreshToken);
        refreshTokenRepository.deleteById(refreshInfo.getJwtId());

        log.info("Logout success");
    }

}
