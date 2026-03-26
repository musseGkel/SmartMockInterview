package com.smartmock.interview.auth.application;

import com.smartmock.interview.auth.domain.UserAccount;
import org.springframework.http.ResponseCookie;

public record AuthResult(
        UserAccount user,
        String accessToken,
        long expiresIn,
        ResponseCookie refreshCookie) {
}