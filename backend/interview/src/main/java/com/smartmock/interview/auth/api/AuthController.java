package com.smartmock.interview.auth.api;

import com.smartmock.interview.auth.api.dto.AuthResponse;
import com.smartmock.interview.auth.api.dto.LoginRequest;
import com.smartmock.interview.auth.api.dto.RefreshResponse;
import com.smartmock.interview.auth.api.dto.RegisterRequest;
import com.smartmock.interview.auth.application.AuthResult;
import com.smartmock.interview.auth.application.AuthService;
import com.smartmock.interview.auth.application.RefreshResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = authService.register(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(authService.toAuthResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(authService.toAuthResponse(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(HttpServletRequest request) {
        String refreshToken = authService.extractRefreshTokenFromCookieHeader(request.getHeader("Cookie"));
        RefreshResult result = authService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authService.logout().toString())
                .build();
    }
}