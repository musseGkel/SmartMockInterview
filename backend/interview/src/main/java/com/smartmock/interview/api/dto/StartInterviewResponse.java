package com.smartmock.interview.api.dto;

public record StartInterviewResponse(
    String sessionId,
    String question,
    String topic
) {
}