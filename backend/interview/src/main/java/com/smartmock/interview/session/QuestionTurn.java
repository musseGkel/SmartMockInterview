package com.smartmock.interview.session;

import com.smartmock.interview.dto.FeedbackDto;

public record QuestionTurn(
        String question,
        String answer,
        FeedbackDto feedback) {
}