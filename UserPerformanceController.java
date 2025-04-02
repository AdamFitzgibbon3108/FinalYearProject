package com.example.controller;

import com.example.service.UserPerformanceService;
import dto.UserPerformanceDTO;
import dto.UserQuizHistoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class UserPerformanceController {

    private final UserPerformanceService userPerformanceService;
    private final ObjectMapper objectMapper;

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
        Map<String, Double> categoryScores = userPerformanceService.getAverageScorePerCategory(username);
        Map<String, Integer> timelineScores = userPerformanceService.getScoreTimeline(username);

        try {
            String categoryScoresJson = objectMapper.writeValueAsString(categoryScores);
            String timelineScoresJson = objectMapper.writeValueAsString(timelineScores);

            model.addAttribute("performance", performance);
            model.addAttribute("categoryScoresJson", categoryScoresJson);
            model.addAttribute("timelineScoresJson", timelineScoresJson);
        } catch (JsonProcessingException e) {
            model.addAttribute("categoryScoresJson", "{}");
            model.addAttribute("timelineScoresJson", "{}");
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
