package com.smartmock.interview.auth.application;

import org.springframework.http.ResponseCookie;

public record AuthTokens(
        String accessToken,
        long expiresIn,
        ResponseCookie refreshCookie
) {}