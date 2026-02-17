package com.smartmock.interview.ai;

import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.dto.FeedbackDto;
import com.smartmock.interview.dto.GeneratedQuestion;
import com.smartmock.interview.session.QuestionTurn;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewAiService {

    private final ChatClient chatClient;

    public InterviewAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Generate the first (or next) question for a given domain.
     */
    public GeneratedQuestion generateFirstQuestion(InterviewDomain domain, List<QuestionTurn> history) {
        String historyText = history == null || history.isEmpty()
                ? "No previous questions have been asked."
                : buildHistorySummary(history);

        String prompt = """
                You are an expert technical interview coach.

                Domain: %s

                Conversation history:
                %s

                Your task:
                - Choose the most appropriate next topic within this domain.
                - Generate ONE clear, specific interview question for that topic.
                - Do NOT include an answer.

                Respond ONLY as strict JSON in this shape:
                {
                  "topic": "<short topic name>",
                  "question": "<the full question text>"
                }
                """.formatted(domain.name(), historyText);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(GeneratedQuestion.class);
    }

    /**
     * Evaluate a candidate's answer and return structured feedback.
     */
    public FeedbackDto evaluateAnswer(
            InterviewDomain domain,
            String topic,
            String question,
            String answer,
            List<QuestionTurn> history) {
        String historyText = history == null || history.isEmpty()
                ? "No previous questions have been asked."
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
                """.formatted(domain.name(), topic, historyText, question, answer);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<FeedbackDto>() {
                });
    }

    private String buildHistorySummary(List<QuestionTurn> history) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            QuestionTurn turn = history.get(i);
            sb.append("Q").append(i + 1).append(": ").append(turn.question()).append("\n")
                    .append("A").append(i + 1).append(": ").append(turn.answer()).append("\n")
                    .append("Feedback score: ").append(turn.feedback().score()).append("\n")
                    .append("Summary: ").append(turn.feedback().summary()).append("\n\n");
        }
        return sb.toString();
    }
}