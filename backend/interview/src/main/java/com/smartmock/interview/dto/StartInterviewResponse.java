package com.smartmock.interview.dto;

public record StartInterviewResponse(
    String sessionId,
    String question,
    String topic
) {
}