package com.smartmock.interview.api.dto;

import java.util.List;

import com.smartmock.interview.domain.QuestionTurn;

public record InterviewHistoryDetailResponse(
                String sessionId,
                String domain,
                String ownerUserId,
                String state,
                String currentQuestion,
                List<QuestionTurn> history) {
}