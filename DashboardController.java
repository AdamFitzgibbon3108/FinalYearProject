package com.example.controller;

import com.example.service.QuestionService;
import com.example.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final QuestionService questionService;
    private final ScoringService scoringService;

    @Autowired
    public DashboardController(QuestionService questionService, ScoringService scoringService) {
        this.questionService = questionService;
        this.scoringService = scoringService;
    }

    /**
     * Display the dashboard page.
     *
     * @param model The model to pass data to the view.
     * @return The dashboard view name.
     */
    @GetMapping
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);
        model.addAttribute("welcomeMessage", "Welcome to your Dashboard!");

        return "dashboard";
    }

    /**
     * Display the questionnaire page.
     *
     * @param model The model to pass data to the view.
     * @return The questionnaire view name.
     */
    @GetMapping("/questionnaire")
    public String showQuestionnaire(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Extract user role (default to "employee" if no role is found)
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                .orElse("employee");

        // Fetch questions based on the role using the QuestionService
        List<Map<String, Object>> questions = questionService.getQuestionsForRole(role);

        // Debugging: Log questions to verify
        System.out.println("Role: " + role);
        System.out.println("Questions: " + questions);

        model.addAttribute("username", username);
        model.addAttribute("role", role); // Pass the role for context if needed
        model.addAttribute("questions", questions); // Pass questions as a list of maps
        return "questionnaire";
    }

    /**
     * Handle the submission of questionnaire responses.
     *
     * @param responses The list of responses submitted by the user.
     * @return A success message or an error message.
     */
    @PostMapping("/responses/submit")
    public String submitResponses(@RequestParam Map<String, String> responses, Model model) {
        try {
            // Calculate score based on responses
            int score = scoringService.calculateScore(responses);

            // Save responses to the database (if implemented in ScoringService)
            scoringService.saveResponses(responses);

            // Debugging: Log user responses and score
            System.out.println("Responses: " + responses);
            System.out.println("Score: " + score);

            // Pass score and responses to the result page
            model.addAttribute("score", score);
            model.addAttribute("responses", responses);

            return "result";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while processing your responses. Please try again.");
            return "error";
        }
    }
}
