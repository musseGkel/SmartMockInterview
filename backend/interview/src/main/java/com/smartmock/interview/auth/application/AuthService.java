package com.smartmock.interview.auth.application;

import com.smartmock.interview.auth.api.dto.AuthResponse;
import com.smartmock.interview.auth.api.dto.LoginRequest;
import com.smartmock.interview.auth.api.dto.RefreshResponse;
import com.smartmock.interview.auth.api.dto.RegisterRequest;
import com.smartmock.interview.auth.domain.UserAccount;
import com.smartmock.interview.auth.infrastructure.JwtService;
import com.smartmock.interview.auth.persistence.UserRepository;
import com.smartmock.interview.config.AuthCookieProperties;
import com.smartmock.interview.config.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthCookieProperties authCookieProperties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       JwtProperties jwtProperties,
                       AuthCookieProperties authCookieProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.authCookieProperties = authCookieProperties;
    }

    public AuthResult register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new IllegalArgumentException("Email is already registered");
        });

        String passwordHash = passwordEncoder.encode(request.password());
        UserAccount user = new UserAccount(normalizedEmail, passwordHash, DEFAULT_ROLE);
        UserAccount savedUser = userRepository.save(user);

        return issueAuthResult(savedUser);
    }

    public AuthResult login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        UserAccount user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return issueAuthResult(user);
    }

    public RefreshResult refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new RefreshResult(
                new RefreshResponse(
                        newAccessToken,
                        "Bearer",
                        jwtService.getAccessTokenExpiresInSeconds()
                ),
                buildRefreshCookie(newRefreshToken)
        );
    }

    public ResponseCookie logout() {
        return deleteRefreshCookie();
    }

    public String extractRefreshTokenFromCookieHeader(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        String cookieName = authCookieProperties.refreshCookieName();

        return Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .filter(cookie -> cookie.startsWith(cookieName + "="))
                .map(cookie -> cookie.substring((cookieName + "=").length()))
                .findFirst()
                .orElse(null);
    }

    public AuthResponse toAuthResponse(AuthResult result) {
        return new AuthResponse(
                result.accessToken(),
                "Bearer",
                result.expiresIn(),
                result.user().getId(),
                result.user().getEmail()
        );
    }

    private AuthResult issueAuthResult(UserAccount user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResult(
                user,
                accessToken,
                jwtService.getAccessTokenExpiresInSeconds(),
                buildRefreshCookie(refreshToken)
        );
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(authCookieProperties.refreshCookieName(), refreshToken)
                .httpOnly(true)
                .secure(authCookieProperties.cookieSecure())
                .sameSite(authCookieProperties.sameSite())
                .path(authCookieProperties.refreshCookiePath())
                .maxAge(Duration.ofDays(jwtProperties.refreshTtlDays()))
                .build();
    }

    private ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from(authCookieProperties.refreshCookieName(), "")
                .httpOnly(true)
                .secure(authCookieProperties.cookieSecure())
                .sameSite(authCookieProperties.sameSite())
                .path(authCookieProperties.refreshCookiePath())
                .maxAge(Duration.ZERO)
                .build();
    }

    private String normalizeEmail(String email) {
        return Optional.ofNullable(email)
                .map(String::trim)
                .map(String::toLowerCase)
                .orElseThrow(() -> new IllegalArgumentException("Email is required"));
    }
}