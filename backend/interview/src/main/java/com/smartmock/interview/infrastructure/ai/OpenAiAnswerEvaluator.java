package com.smartmock.interview.infrastructure.ai;

import com.smartmock.interview.application.port.AnswerEvaluator;
import com.smartmock.interview.domain.*;
import com.smartmock.interview.domain.QuestionTurn;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAiAnswerEvaluator implements AnswerEvaluator {

    private final ChatClient chatClient;

    public OpenAiAnswerEvaluator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Feedback evaluate(
            InterviewDomain domain,
            String topic,
            String question,
            String answer,
            List<QuestionTurn> history) {
        String historyText = history == null || history.isEmpty()
                ? "No previous questions."
                : buildHistorySummary(history);

        String prompt = """
                You are an expert interview coach for the domain: %s.

                Current topic: %s

                Conversation history (previous questions, answers, and feedback):
                %s

                Current question:
                "%s"

                Candidate's answer:
                "%s"

                Your task:
                - Evaluate the answer strictly for correctness, depth, clarity, and practical understanding.
                - Give a score from 1 to 5 (5 is excellent).
                - List specific strengths as short bullet points.
                - List specific improvements as short bullet points.
                - Add a short, friendly summary.

                Respond ONLY as strict JSON in this shape:
                {
                  "score": 1-5,
                  "strengths": ["..."],
                  "improvements": ["..."],
                  "summary": "..."
                }
                """
                .formatted(domain.name(), topic, historyText, question, answer);

        var dto = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<Feedback>() {
                });

        return new Feedback(dto.score(), dto.strengths(), dto.improvements(), dto.summary());
    }

    private String buildHistorySummary(List<QuestionTurn> history) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            QuestionTurn t = history.get(i);
            sb.append("Q").append(i + 1).append(": ").append(t.question())
                    .append(" | A: ").append(t.answer())
                    .append(" | Score: ").append(t.feedback().score()).append("\n");
        }
        return sb.toString();
    }

}