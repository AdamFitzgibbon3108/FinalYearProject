package com.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "survey_question")
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false)
    private String questionText;
    
    @Column(name = "answer_type", nullable = false)
    private String answerType; // "text" or "multiple_choice"

    @OneToMany(mappedBy = "surveyQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyQuestionOptions> options; // Predefined multiple-choice options

    // Constructors
    public SurveyQuestion() {}

    public SurveyQuestion(String questionText, List<SurveyQuestionOptions> options, String answerType) {
        this.questionText = questionText;
        this.options = options;
        this.answerType = answerType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getAnswerType() { return answerType; }
    public void setAnswerType(String answerType) { this.answerType = answerType; }

    public List<SurveyQuestionOptions> getOptions() { return options; }
    public void setOptions(List<SurveyQuestionOptions> options) { this.options = options; }
}

