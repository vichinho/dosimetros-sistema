package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.auth.LoginRequest;
import com.dosimetros.backend.dto.auth.LoginResponse;
import com.dosimetros.backend.security.LoginRateLimiter;
import com.dosimetros.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter rateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter rateLimiter) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest http) {
        String ip = clientIp(http);
        rateLimiter.verificarPermitido(ip);
        try {
            LoginResponse response = authService.login(request);
            rateLimiter.registrarExito(ip);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            rateLimiter.registrarFallo(ip);
            throw e;
        }
    }

    private String clientIp(HttpServletRequest http) {
        String xff = http.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return http.getRemoteAddr();
    }
}
