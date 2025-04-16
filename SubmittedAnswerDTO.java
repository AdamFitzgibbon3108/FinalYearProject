package dto;

import java.time.LocalDateTime;

public class SubmittedAnswerDTO {

    private Long questionId;
    private String selectedAnswer;
    private String correctAnswer;
    private String category;
    private String role;
    private LocalDateTime timestamp;

    public SubmittedAnswerDTO() {
    }

    public SubmittedAnswerDTO(Long questionId, String selectedAnswer, String correctAnswer,
                               String category, String role, LocalDateTime timestamp) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.role = role;
        this.timestamp = timestamp;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SubmittedAnswerDTO{" +
                "questionId=" + questionId +
                ", selectedAnswer='" + selectedAnswer + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", category='" + category + '\'' +
                ", role='" + role + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

