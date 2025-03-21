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
    private String answer;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String category;

    //  Default constructor
    public Response() {}

    //  Fixed constructor to properly initialize values
    public Response(Question question, User user, String answer, int score, LocalDateTime timestamp, String role, String category) {
        this.question = question;
        this.user = user;
        this.answer = answer;
        this.score = score;
        this.timestamp = timestamp;
        this.role = role;
        this.category = category != null ? category : "Unknown"; // Prevents null values
    }

    //  Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category != null ? category : "Unknown"; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    //  Debugging method to print response details
    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", question=" + (question != null ? question.getId() : "null") +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", answer='" + answer + '\'' +
                ", score=" + score +
                ", timestamp=" + timestamp +
                ", role='" + role + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
