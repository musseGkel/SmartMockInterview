package com.smartmock.interview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
                String issuer,
                int accessTtlMinutes,
                int refreshTtlDays,
                String secret) {
}