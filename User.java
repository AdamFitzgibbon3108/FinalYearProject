package com.example.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore; // ADD THIS

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
	private boolean surveyCompleted = false;

	@Column(nullable = true)
	private String recommendedSecurityCategory;

	// FIELDS FOR PROFILE
	@Column(nullable = true)
	private String fullName;

	@Column(nullable = true)
	private String email;

	@Column(nullable = true)
	private String phoneNumber;

	@Column(nullable = true)
	private String address;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore // <--THIS TO PREVENT INFINITE LOOP
	private List<SurveyResponse> surveyResponses;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	// Constructors
	public User() {
	}

	public User(String username, String password, Set<Role> roles) {
		this.username = username;
		this.password = password;
		this.roles = (roles != null) ? roles : new HashSet<>();
		this.surveyCompleted = false;
		this.recommendedSecurityCategory = null;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSurveyCompleted() {
		return surveyCompleted;
	}

	public void setSurveyCompleted(boolean surveyCompleted) {
		this.surveyCompleted = surveyCompleted;
	}

	public String getRecommendedSecurityCategory() {
		return recommendedSecurityCategory;
	}

	public void setRecommendedSecurityCategory(String recommendedSecurityCategory) {
		this.recommendedSecurityCategory = recommendedSecurityCategory;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<SurveyResponse> getSurveyResponses() {
		return surveyResponses;
	}

	public void setSurveyResponses(List<SurveyResponse> surveyResponses) {
		this.surveyResponses = surveyResponses;
	}

	public Set<Role> getRoles() {
		return roles;
	}

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
		return "User{" + "id=" + id + ", username='" + username + '\'' + ", fullName='" + fullName + '\'' + ", email='"
				+ email + '\'' + ", roles=" + roles + ", surveyCompleted=" + surveyCompleted
				+ ", recommendedSecurityCategory='" + recommendedSecurityCategory + '\'' + '}';
	}
}
