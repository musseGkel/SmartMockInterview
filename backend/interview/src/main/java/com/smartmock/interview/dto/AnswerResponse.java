package com.smartmock.interview.dto;

public record AnswerResponse(
    FeedbackDto feedback,
    String nextQuestion,
    String topic,
    boolean finished
) {
}