package com.smartmock.interview.dto;

import java.util.List;

public record FeedbackDto(
                int score, List<String> strengths,
                List<String> improvements, String summary) {

}
