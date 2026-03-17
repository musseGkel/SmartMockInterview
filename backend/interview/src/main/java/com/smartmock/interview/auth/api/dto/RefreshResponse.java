package com.smartmock.interview.auth.api.dto;

public record RefreshResponse(
        String accessToken,
        String tokenType,
        long expiresIn) {
}