package dto;

import java.time.LocalDateTime;

public class UserQuizSummaryDTO {
	private String category;
	private String role;
	private int score;
	private int totalQuestions;
	private LocalDateTime completedAt;

	public UserQuizSummaryDTO(String category, String role, int score, int totalQuestions, LocalDateTime completedAt) {
		this.category = category;
		this.role = role;
		this.score = score;
		this.totalQuestions = totalQuestions;
		this.completedAt = completedAt;
	}

	public String getCategory() {
		return category;
	}

	public String getRole() {
		return role;
	}

	public int getScore() {
		return score;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}
}
