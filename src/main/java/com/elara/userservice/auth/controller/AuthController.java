package com.elara.userservice.auth.controller;


import com.elara.userservice.auth.dto.AuthRequest;
import com.elara.userservice.auth.dto.AuthResponse;
import com.elara.userservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<AuthResponse> logout() {
        return ResponseEntity.ok(AuthResponse.builder()
                        .accessToken("token")
                        .refreshToken("refresh")
                        .build());
    }
}
