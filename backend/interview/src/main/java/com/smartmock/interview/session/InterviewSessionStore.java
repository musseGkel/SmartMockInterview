package com.smartmock.interview.session;

import com.smartmock.interview.domain.InterviewDomain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InterviewSessionStore {

    private final Map<String, InterviewSession> sessions = new ConcurrentHashMap<>();

    public InterviewSession create(InterviewDomain domain) {
        InterviewSession session = new InterviewSession(domain);
        sessions.put(session.getId(), session);
        return session;
    }

    public Optional<InterviewSession> findById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public InterviewSession requireById(String sessionId) {
        InterviewSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return session;
    }

    public void save(InterviewSession session) {
        sessions.put(session.getId(), session);
    }

    public void delete(String sessionId) {
        sessions.remove(sessionId);
    }
}