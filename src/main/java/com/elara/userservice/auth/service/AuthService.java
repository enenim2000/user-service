package com.elara.userservice.auth.service;

import com.elara.userservice.auth.dto.AuthRequest;
import com.elara.userservice.auth.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    public AuthResponse register(AuthRequest authRequest) {
        String accessToken = jwtService.generate(authRequest.getEmail(), "ACCESS");
        String refreshToken = jwtService.generate(authRequest.getEmail(), "REFRESH");

        return new AuthResponse(accessToken, refreshToken);
    }
}
