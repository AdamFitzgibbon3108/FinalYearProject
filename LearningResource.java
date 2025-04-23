package com.example.model;

import jakarta.persistence.*;

@Entity
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String url;

    @ManyToOne
    @JoinColumn(name = "control_id") //  MATCHES DB column name
    private SecurityControl control;


    public LearningResource() {
    }

    public LearningResource(String title, String url, SecurityControl control) {
        this.title = title;
        this.url = url;
        this.control = control;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SecurityControl getControl() {
        return control;
    }

    public void setControl(SecurityControl control) {
        this.control = control;
    }
}

