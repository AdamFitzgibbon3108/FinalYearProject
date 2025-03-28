package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "survey_question_options")
public class SurveyQuestionOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @ManyToOne
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SurveyQuestion surveyQuestion;

    // Constructors
    public SurveyQuestionOptions() {}

    public SurveyQuestionOptions(String optionValue, SurveyQuestion surveyQuestion) {
        this.optionValue = optionValue;
        this.surveyQuestion = surveyQuestion;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOptionValue() { return optionValue; }
    public void setOptionValue(String optionValue) { this.optionValue = optionValue; }

    public SurveyQuestion getSurveyQuestion() { return surveyQuestion; }
    public void setSurveyQuestion(SurveyQuestion surveyQuestion) { this.surveyQuestion = surveyQuestion; }
}
