package com.example.controller;

import com.example.model.LearningResource;
import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.model.SecurityControl;
import com.example.service.LearningResourceService;
import com.example.service.QuizResultService;
import com.example.service.ResponseService;
import com.example.service.ScoringService;
import com.example.service.SecurityControlService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final ResponseService responseService;
    private final QuizResultService quizResultService;
    private final UserService userService;
    private final SecurityControlService securityControlService;
    private final LearningResourceService learningResourceService;
    private final ScoringService scoringService; // ✅ Injected

    @Autowired
    public ResultController(ResponseService responseService,
                            QuizResultService quizResultService,
                            UserService userService,
                            SecurityControlService securityControlService,
                            LearningResourceService learningResourceService,
                            ScoringService scoringService) {
        this.responseService = responseService;
        this.quizResultService = quizResultService;
        this.userService = userService;
        this.securityControlService = securityControlService;
        this.learningResourceService = learningResourceService;
        this.scoringService = scoringService;
    }

    @GetMapping
    public String showResultPage(Model model,
                                 @RequestParam(name = "fromReview", required = false, defaultValue = "false") boolean fromReview) {
        System.out.println(">>>> [DEBUG] Entered /result controller method");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).orElseThrow().getId();

        List<Response> responses = responseService.getResponsesByUsername(username);
        List<QuizResult> results = quizResultService.findByUserId(userId);

        QuizResult latestResult = results.stream()
                .max(Comparator.comparing(QuizResult::getCompletedAt))
                .orElse(null);

        if (latestResult != null) {
            boolean passed = Boolean.TRUE.equals(latestResult.getPassed());
            String recommendations = latestResult.getRecommendations();

            if (recommendations == null || recommendations.trim().isEmpty()) {
                recommendations = scoringService.getCategoryRecommendation(latestResult.getCategory()); // ✅ use category-specific recommendation
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

            // Learning Resources Integration
            String categoryName = latestResult.getCategory();
            System.out.println("[DEBUG] Attempting to match SecurityControl by category: '" + categoryName + "'");
            System.out.println("[FINAL DEBUG] QuizResult category string: --> '" + categoryName + "'");
            System.out.println(">>> [TRACE] About to print all controls...");
            securityControlService.printAllControlNames();

            Optional<SecurityControl> optionalControl = securityControlService.findByNameIgnoreCase(categoryName.trim());

            if (optionalControl.isPresent()) {
                SecurityControl control = optionalControl.get();
                System.out.println("[DEBUG] Match found. SecurityControl ID: " + control.getId());

                List<LearningResource> resources = learningResourceService.getResourcesByControl(control);
                System.out.println("[DEBUG] Retrieved " + resources.size() + " learning resource(s) for category.");
                for (LearningResource r : resources) {
                    System.out.println("  - " + r.getTitle() + ": " + r.getUrl());
                }

                model.addAttribute("resources", resources);
            } else {
                System.out.println("[DEBUG] No SecurityControl found for category: '" + categoryName + "'");
                model.addAttribute("resources", List.of());
            }

            // Filter responses for this specific quiz result
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
            model.addAttribute("resources", List.of());

            model.addAttribute("correctResponses", List.of());
            model.addAttribute("incorrectResponses", List.of());
        }

        model.addAttribute("username", username);
        model.addAttribute("fromReview", fromReview);

        return "result";
    }
}
