package com.example.controller;

import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.service.QuizResultService;
import com.example.service.ResponseService;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final ResponseService responseService;
    private final QuizResultService quizResultService;
    private final UserService userService;

    @Autowired
    public ResultController(ResponseService responseService,
                            QuizResultService quizResultService,
                            UserService userService) {
        this.responseService = responseService;
        this.quizResultService = quizResultService;
        this.userService = userService;
    }

    @GetMapping
    public String showResultPage(Model model,
                                 @RequestParam(name = "fromReview", required = false, defaultValue = "false") boolean fromReview) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).orElseThrow().getId();

        List<Response> responses = responseService.getResponsesByUsername(username);
        List<QuizResult> results = quizResultService.findByUserId(userId);

        QuizResult latestResult = results.stream()
                .max(Comparator.comparing(QuizResult::getCompletedAt))
                .orElse(null);

        if (latestResult != null) {
            boolean passed = latestResult.isPassed();
            String recommendations = latestResult.getRecommendations();
            if (recommendations == null || recommendations.trim().isEmpty()) {
                recommendations = "No specific advice — please review general cybersecurity principles.";
            }

            int total = latestResult.getTotalQuestions();

            System.out.println("===== [DEBUG] Quiz Result Loaded =====");
            System.out.println("Username: " + username);
            System.out.println("Score: " + latestResult.getTotalScore());
            System.out.println("Total Questions: " + total);
            System.out.println("Passed: " + passed);
            System.out.println("Category: " + latestResult.getCategory());
            System.out.println("Role: " + latestResult.getRole());
            System.out.println("======================================");

            model.addAttribute("score", latestResult.getTotalScore());
            model.addAttribute("totalQuestions", total);
            model.addAttribute("passed", passed);
            model.addAttribute("recommendations", recommendations);
            model.addAttribute("category", latestResult.getCategory());
            model.addAttribute("role", latestResult.getRole());

            // Filter responses for this specific quiz result (null-safe)
            List<Response> latestResponses = responses.stream()
                    .filter(r -> r.getQuizResult() != null && r.getQuizResult().getId().equals(latestResult.getId()))
                    .collect(Collectors.toList());

            List<Response> correctResponses = latestResponses.stream()
                    .filter(Response::isCorrect)
                    .collect(Collectors.toList());

            List<Response> incorrectResponses = latestResponses.stream()
                    .filter(r -> !r.isCorrect())
                    .collect(Collectors.toList());

            model.addAttribute("correctResponses", correctResponses);
            model.addAttribute("incorrectResponses", incorrectResponses);
        } else {
            System.out.println("===== [DEBUG] No Quiz Result Found for user: " + username + " =====");

            model.addAttribute("score", "N/A");
            model.addAttribute("totalQuestions", "N/A");
            model.addAttribute("passed", false);
            model.addAttribute("recommendations", "No data available.");
            model.addAttribute("category", "N/A");
            model.addAttribute("role", "N/A");

            model.addAttribute("correctResponses", List.of());
            model.addAttribute("incorrectResponses", List.of());
        }

        model.addAttribute("username", username);
        model.addAttribute("fromReview", fromReview);

        return "result";
    }
}
