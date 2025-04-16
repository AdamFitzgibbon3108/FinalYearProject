package com.example.controller;

import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.model.User;
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

@Controller
@RequestMapping("/result")
public class ReviewController {

    private final UserService userService;
    private final QuizResultService quizResultService;
    private final ResponseService responseService;

    @Autowired
    public ReviewController(UserService userService,
                            QuizResultService quizResultService,
                            ResponseService responseService) {
        this.userService = userService;
        this.quizResultService = quizResultService;
        this.responseService = responseService;
    }

    @GetMapping("/review")
    public String showReviewPage(Model model,
                                 @RequestParam(name = "fromResult", required = false, defaultValue = "false") boolean fromResult) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("DEBUG: Authenticated user -> " + username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<QuizResult> results = quizResultService.findByUserId(user.getId());
        QuizResult latestResult = results.stream()
                .max(Comparator.comparing(QuizResult::getCompletedAt))
                .orElse(null);

        if (latestResult == null) {
            System.out.println("DEBUG: No quiz results found for user.");
            model.addAttribute("error", "No quiz results found.");
            return "error";
        }

        System.out.println("DEBUG: Latest quiz result found -> ID: " + latestResult.getId() +
                ", Category: " + latestResult.getCategory() + ", Role: " + latestResult.getRole());

        List<Response> responses = responseService.getResponsesByQuizResultId(latestResult.getId());

        System.out.println("DEBUG: Fetched " + responses.size() + " responses for latest quiz result:");
        for (Response r : responses) {
            System.out.println("- Question: " + (r.getQuestion() != null ? r.getQuestion().getText() : "null"));
            System.out.println("  Selected: " + r.getSelectedAnswer());
            System.out.println("  Correct:  " + r.getCorrectAnswer());
            System.out.println("  Is Correct: " + r.isCorrect());
        }

        model.addAttribute("responses", responses);
        model.addAttribute("category", latestResult.getCategory());
        model.addAttribute("role", latestResult.getRole());
        model.addAttribute("score", latestResult.getTotalScore());
        model.addAttribute("quizResult", latestResult);
        model.addAttribute("fromResultPage", fromResult); //  Use this key to align with Thymeleaf template

        return "review-answers";
    }
}

