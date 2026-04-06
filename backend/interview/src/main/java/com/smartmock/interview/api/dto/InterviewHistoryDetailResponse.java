package com.smartmock.interview.api.dto;

import java.util.List;

public record InterviewHistoryDetailResponse(
        String sessionId,
        String domain,
        String ownerUserId,
        String state,
        String currentQuestion,
        List<String> history) {
}