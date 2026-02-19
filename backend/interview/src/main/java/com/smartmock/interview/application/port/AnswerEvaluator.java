package com.smartmock.interview.application.port;

import com.smartmock.interview.domain.Feedback;
import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.domain.QuestionTurn;

import java.util.List;

public interface AnswerEvaluator {

    Feedback evaluate(
            InterviewDomain domain,
            String topic,
            String question,
            String answer,
            List<QuestionTurn> history);
}