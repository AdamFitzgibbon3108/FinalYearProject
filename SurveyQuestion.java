package com.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;
    
    @ElementCollection
    private List<String> options; // Multiple-choice options
    
    private String answerType; // "text" or "multiple_choice"

    // Constructors
    public SurveyQuestion() {}

    public SurveyQuestion(String questionText, List<String> options, String answerType) {
        this.questionText = questionText;
        this.options = options;
        this.answerType = answerType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getAnswerType() { return answerType; }
    public void setAnswerType(String answerType) { this.answerType = answerType; }
}

