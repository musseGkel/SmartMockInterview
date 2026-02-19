package com.smartmock.interview.api.dto;

import com.smartmock.interview.domain.Feedback;

public record AnswerResponse(
        Feedback feedback,
        String nextQuestion,
        String topic,
        boolean finished) {
}