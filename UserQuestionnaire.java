package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_questionnaires")
public class UserQuestionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", nullable = false) 
    private Questionnaire questionnaire; 

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "user_questionnaire_questions",
        joinColumns = @JoinColumn(name = "user_questionnaire_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> selectedQuestions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public UserQuestionnaire() {}

    public UserQuestionnaire(User user, Questionnaire questionnaire, List<Question> selectedQuestions) {
        this.user = user;
        this.questionnaire = questionnaire;
        this.selectedQuestions = selectedQuestions;
        this.createdAt = LocalDateTime.now();
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

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<Question> getSelectedQuestions() {
        return selectedQuestions;
    }

    public void setSelectedQuestions(List<Question> selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}



