package com.example.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.service.UserPerformanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.UserPerformanceDTO;
import dto.UserQuizHistoryDTO;

@Controller
public class UserPerformanceController {

	private final UserPerformanceService userPerformanceService;
	private final ObjectMapper objectMapper;
	private static final Logger logger = LoggerFactory.getLogger(UserPerformanceController.class);

	@Autowired
	public UserPerformanceController(UserPerformanceService userPerformanceService, ObjectMapper objectMapper) {
		this.userPerformanceService = userPerformanceService;
		this.objectMapper = objectMapper;
	}

	@GetMapping("/user/performance")
	public String getUserPerformance(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		UserPerformanceDTO performance = userPerformanceService.getUserPerformance(username);
		Map<String, Long> scoreBuckets = userPerformanceService.getScoreBucketDistribution(username);
		Map<String, Long> passFail = userPerformanceService.getPassFailDistribution(username);
		Map<String, Long> categoryGroups = userPerformanceService.getCategoryGroupDistribution(username);
		Map<String, Double> avgCategoryScores = userPerformanceService.getAverageScorePerCategory(username);
		Map<String, Double> avgRoleScores = userPerformanceService.getAverageScorePerRole(username);
		Map<String, Long> quizActivityTimeline = userPerformanceService.getQuizActivityOverTime(username);

		try {
			String scoreBucketsJson = objectMapper.writeValueAsString(scoreBuckets);
			String passFailJson = objectMapper.writeValueAsString(passFail);
			String categoryGroupJson = objectMapper.writeValueAsString(categoryGroups);
			String avgCategoryScoresJson = objectMapper.writeValueAsString(avgCategoryScores);
			String avgRoleScoresJson = objectMapper.writeValueAsString(avgRoleScores);
			String quizActivityTimelineJson = objectMapper.writeValueAsString(quizActivityTimeline);

			logger.info(
					"[{}] - Prepared chart data: scoreBuckets={}, passFail={}, categoryGroups={}, avgScores={}, roleScores={}, quizTimeline={}",
					username, scoreBucketsJson, passFailJson, categoryGroupJson, avgCategoryScoresJson,
					avgRoleScoresJson, quizActivityTimelineJson);

			model.addAttribute("performance", performance);
			model.addAttribute("scoreBucketsJson", scoreBucketsJson);
			model.addAttribute("passFailJson", passFailJson);
			model.addAttribute("categoryGroupJson", categoryGroupJson);
			model.addAttribute("avgCategoryScoresJson", avgCategoryScoresJson);
			model.addAttribute("avgRoleScoresJson", avgRoleScoresJson);
			model.addAttribute("quizActivityTimelineJson", quizActivityTimelineJson);
		} catch (JsonProcessingException e) {
			logger.error("[{}] - Error converting performance data to JSON", username, e);
			model.addAttribute("scoreBucketsJson", "{}");
			model.addAttribute("passFailJson", "{}");
			model.addAttribute("categoryGroupJson", "{}");
			model.addAttribute("avgCategoryScoresJson", "{}");
			model.addAttribute("avgRoleScoresJson", "{}");
			model.addAttribute("quizActivityTimelineJson", "{}");
		}

		return "user-performance";
	}

	@GetMapping("/user/performance/history")
	public String getUserQuizHistory(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		List<UserQuizHistoryDTO> quizHistory = userPerformanceService.getUserQuizHistory(username);

		model.addAttribute("results", quizHistory);
		model.addAttribute("username", username);
		return "user-quiz-history";
	}
}