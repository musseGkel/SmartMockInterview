package com.smartmock.interview.api.dto;

public record InterviewHistoryItemResponse(
        String sessionId,
        String domain,
        String state,
        String currentQuestion,
        int historyCount) {
}