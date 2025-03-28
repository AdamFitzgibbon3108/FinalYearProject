package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citation_url")
    private String citationUrl;
    
    @Column(name = "question_text", nullable = false)
    private String text;

    @Column(name = "question_text", insertable = false, updatable = false) //  Prevents duplicate mapping issues
    private String questionText;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JoinColumn(name = "control_category", referencedColumnName = "id")
    private SecurityControl controlCategory;

    @Column(name = "framework")
    private String framework;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "score")
    private Integer score;

    @Column(name = "role", nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType; // Enum: TRUE_FALSE, MULTIPLE_CHOICE

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "options")
    private String options; // Stored as comma-separated values for multiple-choice

    // Constructors
    public Question() {}

    public Question(String text, String category, SecurityControl controlCategory, String framework,
                    String difficulty, Integer score, String role, QuestionType questionType,
                    String correctAnswer, String options) {
        this.text = text;
        this.category = category;
        this.controlCategory = controlCategory;
        this.framework = framework;
        this.difficulty = difficulty;
        this.score = score;
        this.role = role;
        this.questionType = questionType;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getQuestionText() { return questionText; } // ✅ Read-only
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public SecurityControl getControlCategory() { return controlCategory; }
    public void setControlCategory(SecurityControl controlCategory) { this.controlCategory = controlCategory; }

    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getCitationUrl() {return citationUrl;}
    public void setCitationUrl(String citationUrl) {this.citationUrl = citationUrl;}
    
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
}
