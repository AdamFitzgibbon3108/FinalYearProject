package com.example.controller;

import com.example.model.Question;
import com.example.model.UserQuestionnaire;
import com.example.service.QuestionService;
import com.example.service.UserQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/questionnaire")
public class QuestionnaireSelectionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserQuestionnaireService userQuestionnaireService;

    /**
     * Show the questionnaire selection page.
     */
    @GetMapping("/selection")
    public String showQuestionnaireSelection(Model model) {
        model.addAttribute("allQuestions", questionService.getAllQuestions());
        return "questionnaire-selection";
    }

    /**
     * Generate questionnaire based on role and category selection.
     */
    @PostMapping("/start")
    public String generateRoleBasedQuiz(@RequestParam("selectedRole") String role,
                                        @RequestParam("selectedCategory") String category,
                                        Model model) {
        System.out.println("📌 Received Role: " + role);
        System.out.println("📌 Received Category: " + category);

        List<Question> questions = questionService.getQuestionsByRoleAndCategory(role, category);

        if (questions == null || questions.isEmpty()) {
            System.out.println("⚠️ No questions found for Role: " + role + " and Category: " + category);
            model.addAttribute("errorMessage", "No questions available for the selected role and category.");
            model.addAttribute("questions", null);
            return "questionnaire";
        }

        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedCategory", category);

        return "questionnaire";
    }

    /**
     * Create a custom questionnaire with manually selected questions.
     */
    @PostMapping("/custom")
    public String createCustomQuestionnaire(@RequestParam(value = "selectedQuestions", required = false) List<Long> selectedQuestionIds,
                                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        System.out.println("📌 User " + username + " is creating a custom questionnaire.");

        if (selectedQuestionIds == null || selectedQuestionIds.isEmpty()) {
            System.out.println("⚠️ No questions selected!");
            model.addAttribute("errorMessage", "Please select at least one question.");
            return "questionnaire-selection";
        }

        // Fetch selected questions from database
        List<Question> selectedQuestions = questionService.getQuestionsByIds(selectedQuestionIds);

        if (selectedQuestions.isEmpty()) {
            System.out.println("⚠️ No valid questions retrieved from the database.");
            model.addAttribute("errorMessage", "Invalid question selection. Please try again.");
            return "questionnaire-selection";
        }

        System.out.println("✅ Successfully retrieved " + selectedQuestions.size() + " questions for the custom questionnaire.");

        //  Use the 2-parameter version of createUserQuestionnaire
        UserQuestionnaire userQuestionnaire = userQuestionnaireService.createUserQuestionnaire(username, selectedQuestionIds);

        model.addAttribute("questions", selectedQuestions);
        model.addAttribute("custom", true);

        return "questionnaire";
    }
}
