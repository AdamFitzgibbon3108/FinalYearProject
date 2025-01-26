package com.example.model;

import jakarta.persistence.*;


@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
      
    @Column(name = "question_text", nullable = false)
    private String questionText;

    
    @Column(name = "control_category", nullable = false)
    private String controlCategory;

  
    @Column(name = "framework", nullable = false)
    private String framework;

 
    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "score")
    private int score;

    
    @Column(name = "role", nullable = false) // Added the role column
    private String role;  // New field for role

    // Default constructor
    public Question() {}

    // Constructor with parameters
    public Question(String questionText, String controlCategory, String framework, String difficulty, int score, String role) {
        this.questionText = questionText;
        this.controlCategory = controlCategory;
        this.framework = framework;
        this.difficulty = difficulty;
        this.score = score;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getControlCategory() {
        return controlCategory;
    }

    public void setControlCategory(String controlCategory) {
        this.controlCategory = controlCategory;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // toString() method for debugging and logging
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", controlCategory='" + controlCategory + '\'' +
                ", framework='" + framework + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", score=" + score +
                ", role='" + role + '\'' +
                '}';
    }
}
