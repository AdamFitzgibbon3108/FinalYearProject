package com.example.model;

import jakarta.persistence.*;

@Entity
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private SurveyQuestion question;

    private String response; // User's answer

    // Constructors
    public SurveyResponse() {}

    public SurveyResponse(User user, SurveyQuestion question, String response) {
        this.user = user;
        this.question = question;
        this.response = response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SurveyQuestion getQuestion() { return question; }
    public void setQuestion(SurveyQuestion question) { this.question = question; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
