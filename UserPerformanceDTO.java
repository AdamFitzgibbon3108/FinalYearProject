package dto;

public class UserPerformanceDTO {
    private Long userId;
    private String username;
    private int totalQuizzes;
    private double averageScore;

    public UserPerformanceDTO(Long userId, String username, int totalQuizzes, double averageScore) {
        this.userId = userId;
        this.username = username;
        this.totalQuizzes = totalQuizzes;
        this.averageScore = averageScore;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalQuizzes(int totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}

