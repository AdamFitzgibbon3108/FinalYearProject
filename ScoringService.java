package com.example.service;

import com.example.model.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoringService {

    private static final double PASS_THRESHOLD = 80.0; // You can externalize this if needed

    /**
     * Calculates the total score by comparing each response's selected vs correct answer.
     */
    public int calculateScore(List<Response> responses) {
        return (int) responses.stream()
                .filter(response ->
                        response.getSelectedAnswer() != null &&
                        response.getCorrectAnswer() != null &&
                        response.getSelectedAnswer().equalsIgnoreCase(response.getCorrectAnswer()))
                .count();
    }

    /**
     * Calculates the percentage score from total correct answers and total questions.
     */
    public double calculatePercentage(int totalScore, int totalQuestions) {
        if (totalQuestions == 0) return 0.0;
        return ((double) totalScore / totalQuestions) * 100.0;
    }

    /**
     * Determines whether the user passed or failed based on a threshold.
     */
    public boolean isPassed(double percentage) {
        return percentage >= PASS_THRESHOLD;
    }

    /**
     * Generates a simple recommendation message based on the score.
     */
    public String generateRecommendation(double percentage) {
        if (percentage >= 90) {
            return "Excellent! You're ready for advanced topics.";
        } else if (percentage >= 80) {
            return "Great job! Try mastering deeper concepts.";
        } else if (percentage >= 60) {
            return "You're getting there. Review key topics like authentication, secure development, and system security.";
        } else {
            return "Consider revisiting fundamentals. Focus on secure coding, awareness, and incident response basics.";
        }
    }

    /**
     * Suggests a training program based on performance.
     */
    public String suggestTrainingProgram(double percentage) {
        if (percentage < 60) {
            return "Cybersecurity Essentials Program";
        } else if (percentage < 80) {
            return "Intermediate Secure Coding & Threat Awareness";
        } else {
            return "Advanced Threat Detection & Risk Management";
        }
    }
}


