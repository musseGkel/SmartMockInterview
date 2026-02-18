package com.smartmock.interview.api;

import com.smartmock.interview.ai.InterviewAiService;
import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.dto.*;
import com.smartmock.interview.session.InterviewSession;
import com.smartmock.interview.session.InterviewSessionStore;
import com.smartmock.interview.session.SessionState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private static final int MAX_QUESTIONS = 5;

    private final InterviewSessionStore sessionStore;
    private final InterviewAiService aiService;

    public InterviewController(InterviewSessionStore sessionStore, InterviewAiService aiService) {
        this.sessionStore = sessionStore;
        this.aiService = aiService;
    }

    /**
     * 6.1 GET /api/domains — return list of domain names (strings).
     */
    @GetMapping("/domains")
    public List<String> getDomains() {
        return Arrays.stream(InterviewDomain.values())
                .map(Enum::name)
                .toList();
    }

    /**
     * 6.2 POST /api/interview/start
     * Input: StartInterviewRequest(domain)
     * Validate domain → create session → AI first question → store → return response.
     */
    @PostMapping("/interview/start")
    public StartInterviewResponse startInterview(@RequestBody StartInterviewRequest request) {
        InterviewDomain domain = parseDomain(request.domain());

        InterviewSession session = sessionStore.create(domain);

        var generated = aiService.generateFirstQuestion(domain, session.getHistory());
        session.setCurrentTopic(generated.topic());
        session.setCurrentQuestion(generated.question());
        session.setState(SessionState.AWAITING_ANSWER);
        sessionStore.save(session);

        return new StartInterviewResponse(
                session.getId(),
                session.getCurrentQuestion(),
                session.getCurrentTopic()
        );
    }

    /**
     * 6.3 POST /api/interview/answer
     * Input: AnswerRequest(sessionId, answer)
     * Load session → evaluate → append history → finish or next question → return response.
     */
    @PostMapping("/interview/answer")
    public AnswerResponse submitAnswer(@RequestBody AnswerRequest request) {
        InterviewSession session = sessionStore.findById(request.sessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found: " + request.sessionId()));

        String question = session.getCurrentQuestion();
        String topic = session.getCurrentTopic();
        if (question == null || topic == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No current question for this session");
        }

        FeedbackDto feedback = aiService.evaluateAnswer(
                session.getDomain(),
                topic,
                question,
                request.answer(),
                session.getHistory()
        );

        session.addTurn(question, request.answer(), feedback);

        boolean finished = session.getHistory().size() >= MAX_QUESTIONS;

        if (finished) {
            session.setCurrentQuestion(null);
            session.setCurrentTopic(null);
            session.setState(SessionState.FINISHED);
        } else {
            var next = aiService.generateFirstQuestion(session.getDomain(), session.getHistory());
            session.setCurrentTopic(next.topic());
            session.setCurrentQuestion(next.question());
            session.setState(SessionState.AWAITING_ANSWER);
        }

        sessionStore.save(session);

        return new AnswerResponse(
                feedback,
                finished ? null : session.getCurrentQuestion(),
                finished ? null : session.getCurrentTopic(),
                finished
        );
    }

    private InterviewDomain parseDomain(String domainStr) {
        if (domainStr == null || domainStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "domain is required");
        }
        try {
            return InterviewDomain.valueOf(domainStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown domain: " + domainStr);
        }
    }
}