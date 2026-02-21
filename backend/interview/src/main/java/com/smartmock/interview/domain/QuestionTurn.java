package com.smartmock.interview.domain;

public record QuestionTurn(
                Integer turnNumber,
                String question,
                String answer,
                Feedback feedback) {
}