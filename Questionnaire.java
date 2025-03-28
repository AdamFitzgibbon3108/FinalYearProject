package com.example.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "questionnaires")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private User createdByAdmin;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    @JoinTable(
        name = "user_questionnaire_questions",
        joinColumns = @JoinColumn(name = "questionnaire_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuestionnaire> userQuestionnaires; // ✅ This now correctly references UserQuestionnaire

    // Constructors
    public Questionnaire() {
        this.createdAt = new Date();
    }

    public Questionnaire(String title, String description, User createdByAdmin, List<Question> questions) {
        this.title = title;
        this.description = description;
        this.createdByAdmin = createdByAdmin;
        this.createdAt = new Date();
        this.questions = questions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(User createdByAdmin) { this.createdByAdmin = createdByAdmin; }

    public Date getCreatedAt() { return createdAt; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<UserQuestionnaire> getUserQuestionnaires() { return userQuestionnaires; }
    public void setUserQuestionnaires(List<UserQuestionnaire> userQuestionnaires) {
        this.userQuestionnaires = userQuestionnaires;
    }
}