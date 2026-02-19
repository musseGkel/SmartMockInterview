package com.smartmock.interview.domain;

import java.util.List;

public record Feedback(
                int score, List<String> strengths,
                List<String> improvements, String summary) {

}
