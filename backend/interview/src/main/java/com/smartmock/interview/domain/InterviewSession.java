package com.smartmock.interview.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InterviewSession {

    private final String id;
    private final InterviewDomain domain;
    private final String ownerUserId;
    private SessionState state;
    private String currentTopic;
    private String currentQuestion;
    private final List<QuestionTurn> history = new ArrayList<>();

    public InterviewSession(InterviewDomain domain, String ownerUserId) {
        this.id = UUID.randomUUID().toString();
        this.domain = domain;
        this.ownerUserId = ownerUserId;
        this.state = SessionState.AWAITING_QUESTION;
    }

    public String getId() {
        return id;
    }

    public InterviewDomain getDomain() {
        return domain;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }

    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<QuestionTurn> getHistory() {
        return history;
    }

    public void addTurn(String question, String answer, Feedback feedback) {
        int turnNumber = this.history.size() + 1;
        this.history.add(new QuestionTurn(turnNumber, question, answer, feedback));
    }
}