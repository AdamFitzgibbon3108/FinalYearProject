package dto;

public class UserPerformanceDTO {
    private Long userId;
    private String username;
    private int totalQuizzes;
    private double averageScore;
    private int bestScore;
    private int latestScore;
    private String topCategory;
    private String mostActiveDay;
    private String mostUsedRole;

    public UserPerformanceDTO(Long userId, String username, int totalQuizzes, double averageScore, String mostUsedRole) {
        this.userId = userId;
        this.username = username;
        this.totalQuizzes = totalQuizzes;
        this.averageScore = averageScore;
        this.mostUsedRole = mostUsedRole;
    }
    
    public UserPerformanceDTO(Long userId, String username, int totalQuizzes, double averageScore) {
        this.userId = userId;
        this.username = username;
        this.totalQuizzes = totalQuizzes;
        this.averageScore = averageScore;
    }


    public UserPerformanceDTO(Long userId, String username, int totalQuizzes, double averageScore, int bestScore, int latestScore) {
        this.userId = userId;
        this.username = username;
        this.totalQuizzes = totalQuizzes;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.latestScore = latestScore;
    }

    // Extended constructor for all widgets
    public UserPerformanceDTO(Long userId, String username, int totalQuizzes, double averageScore, int bestScore, int latestScore, String topCategory, String mostActiveDay) {
        this.userId = userId;
        this.username = username;
        this.totalQuizzes = totalQuizzes;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.latestScore = latestScore;
        this.topCategory = topCategory;
        this.mostActiveDay = mostActiveDay;
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

    public int getBestScore() {
        return bestScore;
    }

    public int getLatestScore() {
        return latestScore;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public String getMostActiveDay() {
        return mostActiveDay;
    }
    
    public String getMostUsedRole() {
    	return mostUsedRole;
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

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public void setLatestScore(int latestScore) {
        this.latestScore = latestScore;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }

    public void setMostActiveDay(String mostActiveDay) {
        this.mostActiveDay = mostActiveDay;
    }
    
    public void setMostUsedRole(String mostUsedRole) {
    	this.mostUsedRole = mostUsedRole;
    }
    
    
}
