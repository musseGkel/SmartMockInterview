package com.smartmock.interview.infrastructure.ai;

import com.smartmock.interview.application.port.QuestionGenerator;
import com.smartmock.interview.domain.InterviewDomain;
import com.smartmock.interview.domain.QuestionTurn;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OpenAiQuestionGenerator implements QuestionGenerator {

    private final ChatClient chatClient;

    public OpenAiQuestionGenerator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public GeneratedQuestion generateNext(InterviewDomain domain, List<QuestionTurn> history) {
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

        var result = chatClient.prompt().user(prompt).call().entity(GeneratedQuestionResponse.class);
        return new GeneratedQuestion(result.topic(), result.question());
    }

    private String buildHistorySummary(List<QuestionTurn> history) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            QuestionTurn t = history.get(i);
            sb.append("Q").append(i + 1).append(": ").append(t.question()).append("\n")
                    .append("A").append(i + 1).append(": ").append(t.answer()).append("\n\n");
        }
        return sb.toString();
    }

    private record GeneratedQuestionResponse(String topic, String question) {
    }
}