package com.smartmock.interview.infrastructure.persistence;

import com.smartmock.interview.application.port.InterviewSessionRepository;
import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.domain.InterviewSession;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInterviewSessionRepository implements InterviewSessionRepository {

    private final Map<String, InterviewSession> sessions = new ConcurrentHashMap<>();

    @Override
    public InterviewSession create(InterviewDomain domain, String ownerUserId) {
        InterviewSession session = new InterviewSession(domain, ownerUserId);
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<InterviewSession> findById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public void save(InterviewSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public List<InterviewSession> findAllByOwnerUserId(String ownerUserId) {
        return sessions.values().stream()
                .filter(session -> ownerUserId != null && ownerUserId.equals(session.getOwnerUserId()))
                .toList();
    }
}