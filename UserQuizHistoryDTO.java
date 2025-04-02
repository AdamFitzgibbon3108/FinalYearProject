package dto;

import java.time.LocalDateTime;

public class UserQuizHistoryDTO {

    private String category;
    private String role;
    private int totalScore;
    private int totalQuestions;
    private LocalDateTime completedAt;

    public UserQuizHistoryDTO() {
    }

    public UserQuizHistoryDTO(String category, String role, int totalScore, int totalQuestions, LocalDateTime completedAt) {
        this.category = category;
        this.role = role;
        this.totalScore = totalScore;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public String getRole() {
        return role;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // Setters
    public void setCategory(String category) {
        this.category = category;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

