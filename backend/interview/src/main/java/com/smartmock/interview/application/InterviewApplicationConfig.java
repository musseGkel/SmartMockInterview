package com.smartmock.interview.application;

import com.smartmock.interview.application.port.AnswerEvaluator;
import com.smartmock.interview.application.port.InterviewSessionRepository;
import com.smartmock.interview.application.port.QuestionGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterviewApplicationConfig {

    @Bean
    public InterviewApplicationService interviewApplicationService(
            InterviewSessionRepository sessionRepository,
            QuestionGenerator questionGenerator,
            AnswerEvaluator answerEvaluator) {
        return new InterviewApplicationService(sessionRepository, questionGenerator, answerEvaluator);
    }
}