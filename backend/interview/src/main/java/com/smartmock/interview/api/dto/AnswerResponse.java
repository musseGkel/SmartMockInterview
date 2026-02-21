package com.smartmock.interview.api.dto;

import java.util.List;

import com.smartmock.interview.domain.Feedback;
import com.smartmock.interview.domain.QuestionTurn;

public record AnswerResponse(
                List<QuestionTurn> history,
                Feedback feedback,
                String nextQuestion,
                String topic,
                boolean finished) {
}