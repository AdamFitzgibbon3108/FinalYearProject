package com.example.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean pendingApproval;

    @Column(nullable = false)
    private boolean surveyCompleted = false;

    @Column(nullable = true)
    private String recommendedSecurityCategory;

    @Column(nullable = false)
    private boolean banned = false; // ✅ renamed to lowercase

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyResponse> surveyResponses;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Default Constructor
    public User() {}

    // Constructor with fields
    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = (roles != null) ? roles : new HashSet<>();
        this.surveyCompleted = false;
        this.recommendedSecurityCategory = null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isPendingApproval() { return pendingApproval; }
    public void setPendingApproval(boolean pendingApproval) { this.pendingApproval = pendingApproval; }

    public boolean isSurveyCompleted() { return surveyCompleted; }
    public void setSurveyCompleted(boolean surveyCompleted) { this.surveyCompleted = surveyCompleted; }

    public String getRecommendedSecurityCategory() { return recommendedSecurityCategory; }
    public void setRecommendedSecurityCategory(String recommendedSecurityCategory) {
        this.recommendedSecurityCategory = recommendedSecurityCategory;
    }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

    public List<SurveyResponse> getSurveyResponses() { return surveyResponses; }
    public void setSurveyResponses(List<SurveyResponse> surveyResponses) { this.surveyResponses = surveyResponses; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) {
        this.roles = (roles != null) ? roles : new HashSet<>();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", surveyCompleted=" + surveyCompleted +
                ", recommendedSecurityCategory='" + recommendedSecurityCategory + '\'' +
                ", banned=" + banned +
                '}';
    }
}

