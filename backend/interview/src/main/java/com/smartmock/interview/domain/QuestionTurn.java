package com.smartmock.interview.domain;

public record QuestionTurn(
        String question,
        String answer,
        Feedback feedback) {
}