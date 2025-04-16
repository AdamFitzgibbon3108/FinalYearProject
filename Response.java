package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String selectedAnswer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String category;

    @ManyToOne
    @JoinColumn(name = "quiz_result_id")
    private QuizResult quizResult;

    public Response() {}

    public Response(Question question, User user, String selectedAnswer, String correctAnswer,
                    int score, LocalDateTime timestamp, String role, String category) {
        this.question = question;
        this.user = user;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = selectedAnswer != null && selectedAnswer.equalsIgnoreCase(correctAnswer);
        this.score = score;
        this.timestamp = timestamp;
        this.role = role;
        this.category = category != null ? category : "Unknown";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    //  Thymeleaf-compatible getter
    public boolean isCorrect() {
        return correct;
    }

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category != null ? category : "Unknown"; }

    public QuizResult getQuizResult() { return quizResult; }
    public void setQuizResult(QuizResult quizResult) { this.quizResult = quizResult; }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", question=" + (question != null ? question.getId() : "null") +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", selectedAnswer='" + selectedAnswer + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", correct=" + correct +
                ", score=" + score +
                ", timestamp=" + timestamp +
                ", role='" + role + '\'' +
                ", category='" + category + '\'' +
                ", quizResult=" + (quizResult != null ? quizResult.getId() : "null") +
                '}';
    }
}
