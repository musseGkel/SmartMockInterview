package com.smartmock.interview.application.port;

import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.domain.InterviewSession;

import java.util.List;
import java.util.Optional;

public interface InterviewSessionRepository {

    InterviewSession create(InterviewDomain domain, String ownerUserId);

    Optional<InterviewSession> findById(String sessionId);

    void save(InterviewSession session);

    List<InterviewSession> findAllByOwnerUserId(String ownerUserId);
}