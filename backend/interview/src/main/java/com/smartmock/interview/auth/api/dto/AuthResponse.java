package com.smartmock.interview.auth.api.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String userId,
        String email) {
}