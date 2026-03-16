package com.smartmock.interview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthCookieProperties(
        String refreshCookieName,
        String refreshCookiePath,
        boolean cookieSecure,
        String sameSite) {
}