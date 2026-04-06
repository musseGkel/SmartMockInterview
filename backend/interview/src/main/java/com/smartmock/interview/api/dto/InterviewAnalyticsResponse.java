package com.smartmock.interview.api.dto;

public record InterviewAnalyticsResponse(
        long totalSessions,
        long activeSessions,
        long completedSessions) {
}