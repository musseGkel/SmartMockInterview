package com.smartmock.interview.application;

import com.smartmock.interview.api.dto.AnswerResponse;
import com.smartmock.interview.api.dto.StartInterviewResponse;
import com.smartmock.interview.application.port.AnswerEvaluator;
import com.smartmock.interview.application.port.InterviewSessionRepository;
import com.smartmock.interview.application.port.QuestionGenerator;
import com.smartmock.interview.domain.*;

import java.util.List;
import com.smartmock.interview.api.dto.InterviewAnalyticsResponse;

public class InterviewApplicationService {

    private static final int MAX_QUESTIONS = 5;

    private final InterviewSessionRepository sessionRepository;
    private final QuestionGenerator questionGenerator;
    private final AnswerEvaluator answerEvaluator;

    public InterviewApplicationService(
            InterviewSessionRepository sessionRepository,
            QuestionGenerator questionGenerator,
            AnswerEvaluator answerEvaluator) {
        this.sessionRepository = sessionRepository;
        this.questionGenerator = questionGenerator;
        this.answerEvaluator = answerEvaluator;
    }

    public List<String> listDomains() {
        return List.of(InterviewDomain.values()).stream()
                .map(domain -> domain.name().replace("_", " "))
                .toList();
    }

    public StartInterviewResponse startInterview(String domainStr, String ownerUserId) {
        InterviewDomain domain = parseDomain(domainStr);

        InterviewSession session = sessionRepository.create(domain, ownerUserId);
        QuestionGenerator.GeneratedQuestion next = questionGenerator.generateNext(domain, session.getHistory());

        session.setCurrentTopic(next.topic());
        session.setCurrentQuestion(next.question());
        session.setState(SessionState.AWAITING_ANSWER);
        sessionRepository.save(session);

        return new StartInterviewResponse(
                session.getId(),
                session.getCurrentQuestion(),
                session.getCurrentTopic());
    }

    public AnswerResponse submitAnswer(String sessionId, String answer) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        String question = session.getCurrentQuestion();
        String topic = session.getCurrentTopic();
        if (question == null || topic == null) {
            throw new IllegalArgumentException("No current question for this session");
        }

        Feedback feedback = answerEvaluator.evaluate(
                session.getDomain(),
                topic,
                question,
                answer,
                session.getHistory());

        session.addTurn(question, answer, feedback);
        boolean finished = session.getHistory().size() >= MAX_QUESTIONS;

        if (finished) {
            session.setCurrentQuestion(null);
            session.setCurrentTopic(null);
            session.setState(SessionState.FINISHED);
        } else {
            QuestionGenerator.GeneratedQuestion next = questionGenerator.generateNext(
                    session.getDomain(),
                    session.getHistory());
            session.setCurrentTopic(next.topic());
            session.setCurrentQuestion(next.question());
            session.setState(SessionState.AWAITING_ANSWER);
        }

        sessionRepository.save(session);

        return new AnswerResponse(
                session.getHistory(),
                feedback,
                finished ? null : session.getCurrentQuestion(),
                finished ? null : session.getCurrentTopic(),
                finished);
    }

    private InterviewDomain parseDomain(String domainStr) {
        if (domainStr == null || domainStr.isBlank()) {
            throw new IllegalArgumentException("domain is required");
        }
        try {
            return InterviewDomain.valueOf(domainStr.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown domain: " + domainStr);
        }
    }

    public List<InterviewSession> getHistoryForUser(String ownerUserId) {
        if (ownerUserId == null || ownerUserId.isBlank()) {
            throw new IllegalArgumentException("Owner user id is required");
        }

        return sessionRepository.findAllByOwnerUserId(ownerUserId);
    }

    public InterviewSession getHistorySessionForUser(String ownerUserId, String sessionId) {
        if (ownerUserId == null || ownerUserId.isBlank()) {
            throw new IllegalArgumentException("Owner user id is required");
        }

        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session id is required");
        }

        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Interview session not found"));

        if (session.getOwnerUserId() == null || !session.getOwnerUserId().equals(ownerUserId)) {
            throw new IllegalArgumentException("Interview session not found");
        }

        return session;
    }

    public InterviewAnalyticsResponse getAnalyticsForUser(String ownerUserId) {
        if (ownerUserId == null || ownerUserId.isBlank()) {
            throw new IllegalArgumentException("Owner user id is required");
        }

        List<InterviewSession> sessions = sessionRepository.findAllByOwnerUserId(ownerUserId);

        long totalSessions = sessions.size();

        long activeSessions = sessions.stream()
                .filter(session -> session.getState() != SessionState.FINISHED)
                .count();

        long finishedSessions = sessions.stream()
                .filter(session -> session.getState() == SessionState.FINISHED)
                .count();

        return new InterviewAnalyticsResponse(
                totalSessions,
                activeSessions,
                finishedSessions);
    }

}