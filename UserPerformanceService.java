package com.example.service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.QuizResult;
import com.example.model.SecurityControl;
import com.example.model.User;
import com.example.repository.QuizResultRepository;
import com.example.repository.SecurityControlRepository;
import com.example.repository.UserRepository;

import dto.UserPerformanceDTO;
import dto.UserQuizHistoryDTO;

@Service
public class UserPerformanceService {

	private final UserRepository userRepository;
	private final QuizResultRepository quizResultRepository;
	private final SecurityControlRepository securityControlRepository;
	private static final Logger logger = LoggerFactory.getLogger(UserPerformanceService.class);

	@Autowired
	public UserPerformanceService(UserRepository userRepository, QuizResultRepository quizResultRepository,
			SecurityControlRepository securityControlRepository) {
		this.userRepository = userRepository;
		this.quizResultRepository = quizResultRepository;
		this.securityControlRepository = securityControlRepository;
	}

	public UserPerformanceDTO getUserPerformance(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());
		logger.info("Fetched {} quiz results for user: {}", results.size(), username);

		if (results.isEmpty()) {
			return new UserPerformanceDTO(user.getId(), user.getUsername(), 0, 0.0, 0, 0, "N/A", "N/A");
		}

		int totalQuizzes = results.size();
		double totalScore = results.stream().mapToDouble(QuizResult::getTotalScore).sum();
		double averageScore = totalScore / totalQuizzes;
		int bestScore = results.stream().mapToInt(QuizResult::getTotalScore).max().orElse(0);
		int latestScore = results.stream().max(Comparator.comparing(QuizResult::getCompletedAt))
				.map(QuizResult::getTotalScore).orElse(0);
		String topCategory = results.stream()
				.collect(Collectors.groupingBy(QuizResult::getCategory, Collectors.counting())).entrySet().stream()
				.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("N/A");
		String mostUsedRole = results.stream()
				.collect(Collectors.groupingBy(QuizResult::getRole, Collectors.counting())).entrySet().stream()
				.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("N/A");

		return new UserPerformanceDTO(user.getId(), user.getUsername(), totalQuizzes, averageScore, bestScore,
				latestScore, topCategory, mostUsedRole);
	}

	public List<UserQuizHistoryDTO> getUserQuizHistory(String username) {
		List<QuizResult> results = quizResultRepository.findByUserUsername(username);
		return results
				.stream().map(result -> new UserQuizHistoryDTO(result.getCategory(), result.getRole(),
						result.getTotalScore(), result.getTotalQuestions(), result.getCompletedAt()))
				.collect(Collectors.toList());
	}

	public Map<String, Long> getScoreBucketDistribution(String username) {
		List<QuizResult> results = quizResultRepository.findByUserUsername(username);
		return results.stream().map(r -> {
			int percentage = (r.getTotalQuestions() == 0) ? 0
					: (int) Math.round((r.getTotalScore() * 100.0) / r.getTotalQuestions());
			int bucket = (percentage / 10) * 10;
			return bucket >= 100 ? "100" : bucket + "–" + (bucket + 9);
		}).collect(Collectors.groupingBy(bucket -> bucket, TreeMap::new, Collectors.counting()));
	}

	public Map<String, Long> getPassFailDistribution(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

		return results.stream().map(result -> Boolean.TRUE.equals(result.getPassed()) ? "Pass" : "Fail")
				.collect(Collectors.groupingBy(label -> label, TreeMap::new, Collectors.counting()));
	}

	public Map<String, Long> getCategoryGroupDistribution(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

		Map<String, Long> groupCounts = new TreeMap<>();
		for (QuizResult result : results) {
			String category = result.getCategory();
			SecurityControl control = securityControlRepository.findByNameIgnoreCase(category).orElse(null);
			String group = (control != null && control.getCategoryGroup() != null) ? control.getCategoryGroup()
					: "Other";
			groupCounts.put(group, groupCounts.getOrDefault(group, 0L) + 1);
		}

		return groupCounts;
	}

	public Map<String, Double> getAverageScorePerCategory(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

		logger.info("Calculating average score per category for user: {} with {} quiz results", username,
				results.size());

		Map<String, List<QuizResult>> grouped = results.stream().filter(r -> {
			SecurityControl control = securityControlRepository.findByNameIgnoreCase(r.getCategory()).orElse(null);
			boolean include = control != null && control.getCategoryGroup() != null;
			if (!include) {
				logger.warn("Excluded category '{}': missing or null category group", r.getCategory());
			}
			return include;
		}).collect(Collectors.groupingBy(QuizResult::getCategory));

		logger.info("Grouped into {} categories for averaging", grouped.size());

		Map<String, Double> averages = new TreeMap<>();
		for (Map.Entry<String, List<QuizResult>> entry : grouped.entrySet()) {
			double total = entry.getValue().stream().mapToDouble(QuizResult::getTotalScore).sum();
			double count = entry.getValue().size();
			double average = Math.round((total / count) * 10.0) / 10.0;
			logger.info("Category: {}, Total Score: {}, Attempts: {}, Average: {}", entry.getKey(), total, count,
					average);
			averages.put(entry.getKey(), average);
		}

		return averages;
	}

	public Map<String, Double> getAverageScorePerRole(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

		logger.info("Calculating average score per role for user: {}", username);

		Map<String, List<QuizResult>> grouped = results.stream().filter(r -> r.getRole() != null)
				.collect(Collectors.groupingBy(QuizResult::getRole));

		Map<String, Double> roleAverages = new TreeMap<>();
		for (Map.Entry<String, List<QuizResult>> entry : grouped.entrySet()) {
			double total = entry.getValue().stream().mapToDouble(QuizResult::getTotalScore).sum();
			double count = entry.getValue().size();
			double avg = Math.round((total / count) * 10.0) / 10.0;
			logger.info("Role: {}, Total Score: {}, Attempts: {}, Average: {}", entry.getKey(), total, count, avg);
			roleAverages.put(entry.getKey(), avg);
		}

		return roleAverages;
	}

	public Map<String, Long> getQuizActivityOverTime(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		return results.stream().collect(Collectors.groupingBy(r -> r.getCompletedAt().toLocalDate().format(formatter),
				TreeMap::new, Collectors.counting()));
	}

	public List<String> getWeakestCategories(String username, int limit) {
		Map<String, Double> averages = getAverageScorePerCategory(username);
		return averages.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(limit).map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	// To download recent quizzes (last 10 attempts)
	public List<String> getRecentQuizSummaries(String username) {
		List<QuizResult> results = quizResultRepository.findByUserUsername(username);

		return results.stream().sorted(Comparator.comparing(QuizResult::getCompletedAt).reversed()).limit(10)
				.map(result -> {
					String date = result.getCompletedAt().toLocalDate().toString();
					return date + " - " + result.getCategory() + " (" + result.getRole() + ") - Score: "
							+ result.getTotalScore() + "/" + result.getTotalQuestions();
				}).collect(Collectors.toList());
	}
}
