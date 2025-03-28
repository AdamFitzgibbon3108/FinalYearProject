package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    public QuizResult() {}

    public QuizResult(User user, int totalScore, int totalQuestions, String category, String role) {
        this.user = user;
        this.totalScore = totalScore;
        this.totalQuestions = totalQuestions;
        this.category = category;
        this.role = role;
        this.completedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", totalScore=" + totalScore +
                ", totalQuestions=" + totalQuestions +
                ", category='" + category + '\'' +
                ", role='" + role + '\'' +
                ", completedAt=" + completedAt +
                '}';
    }
}

