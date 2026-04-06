package com.smartmock.interview.api;

import com.smartmock.interview.api.dto.AnswerRequest;
import com.smartmock.interview.api.dto.AnswerResponse;
import com.smartmock.interview.api.dto.InterviewAnalyticsResponse;
import com.smartmock.interview.api.dto.InterviewHistoryDetailResponse;
import com.smartmock.interview.api.dto.InterviewHistoryItemResponse;
import com.smartmock.interview.api.dto.StartInterviewRequest;
import com.smartmock.interview.api.dto.StartInterviewResponse;
import com.smartmock.interview.application.InterviewApplicationService;
import com.smartmock.interview.auth.domain.UserPrincipal;
import com.smartmock.interview.domain.InterviewSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private final InterviewApplicationService applicationService;

    public InterviewController(InterviewApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/domains")
    public List<String> getDomains() {
        return applicationService.listDomains();
    }

    @PostMapping("/interview/start")
    public StartInterviewResponse startInterview(
            @RequestBody StartInterviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        try {
            String ownerUserId = principal != null ? principal.id() : null;
            return applicationService.startInterview(request.domain(), ownerUserId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/interview/answer")
    public AnswerResponse submitAnswer(@RequestBody AnswerRequest request) {
        try {
            return applicationService.submitAnswer(request.sessionId(), request.answer());
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Session not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
    }

    @GetMapping("/interview/analytics")
    public InterviewAnalyticsResponse getAnalytics(
            @AuthenticationPrincipal UserPrincipal principal) {
        return applicationService.getAnalyticsForUser(principal.id());
    }

    @GetMapping("/interview/history")
    public List<InterviewHistoryItemResponse> getHistory(
            @AuthenticationPrincipal UserPrincipal principal) {
        return applicationService.getHistoryForUser(principal.id()).stream()
                .map(session -> new InterviewHistoryItemResponse(
                        session.getId(),
                        session.getDomain().name(),
                        session.getState().name(),
                        session.getCurrentQuestion(),
                        session.getHistory().size()))
                .toList();
    }

    @GetMapping("/interview/history/{sessionId}")
    public InterviewHistoryDetailResponse getHistorySession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        InterviewSession session = applicationService.getHistorySessionForUser(principal.id(), sessionId);

        return new InterviewHistoryDetailResponse(
                session.getId(),
                session.getDomain().name(),
                session.getOwnerUserId(),
                session.getState().name(),
                session.getCurrentQuestion(),
                session.getHistory());
    }
}