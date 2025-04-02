package dto;

import java.util.List;

public class UserQuizPerformanceDTO {

    private String category;
    private String role;
    private double averageScore;
    private int totalQuizzes;
    private List<IndividualQuizResult> quizResults;

    public UserQuizPerformanceDTO(String category, String role, double averageScore, int totalQuizzes, List<IndividualQuizResult> quizResults) {
        this.category = category;
        this.role = role;
        this.averageScore = averageScore;
        this.totalQuizzes = totalQuizzes;
        this.quizResults = quizResults;
    }

    // Getters and setters

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(int totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public List<IndividualQuizResult> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(List<IndividualQuizResult> quizResults) {
        this.quizResults = quizResults;
    }

    // Inner class to represent each quiz result
    public static class IndividualQuizResult {
        private double score;
        private int totalQuestions;
        private String completedAt;

        public IndividualQuizResult(double score, int totalQuestions, String completedAt) {
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.completedAt = completedAt;
        }

        // Getters and setters
        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public String getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(String completedAt) {
            this.completedAt = completedAt;
        }
    }
}

