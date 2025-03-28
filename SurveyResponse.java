package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "survey_response")
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SurveyQuestion surveyQuestion;

    @ManyToOne
    @JoinColumn(name = "survey_question_option_id", nullable = false)
    private SurveyQuestionOptions selectedOption;

    public SurveyResponse() {}

    public SurveyResponse(User user, SurveyQuestion surveyQuestion, SurveyQuestionOptions selectedOption) {
        this.user = user;
        this.surveyQuestion = surveyQuestion;
        this.selectedOption = selectedOption;
    }

    public String getResponse() {
        return selectedOption != null ? selectedOption.getOptionValue() : "Unknown";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SurveyQuestion getSurveyQuestion() { return surveyQuestion; }
    public void setSurveyQuestion(SurveyQuestion surveyQuestion) { this.surveyQuestion = surveyQuestion; }

    public SurveyQuestionOptions getSelectedOption() { return selectedOption; }
    public void setSelectedOption(SurveyQuestionOptions selectedOption) { this.selectedOption = selectedOption; }
}
