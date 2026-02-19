package com.smartmock.interview.application.port;

import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.domain.QuestionTurn;

import java.util.List;

public interface QuestionGenerator {

    record GeneratedQuestion(String topic, String question) {
    }

    GeneratedQuestion generateNext(InterviewDomain domain, List<QuestionTurn> history);
}