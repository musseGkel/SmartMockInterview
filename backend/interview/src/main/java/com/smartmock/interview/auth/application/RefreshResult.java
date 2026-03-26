package com.smartmock.interview.auth.application;

import com.smartmock.interview.auth.api.dto.RefreshResponse;
import org.springframework.http.ResponseCookie;

public record RefreshResult(
        RefreshResponse body,
        ResponseCookie refreshCookie) {
}