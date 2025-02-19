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
        // Debugging logs
        System.out.println("📌 Received Role: " + role);
        System.out.println("📌 Received Category: " + category);
        

        List<Question> questions = questionService.getQuestionsByRoleAndCategory(role, category);
        
        // Debugging if questions are fetched
        if (questions.isEmpty()) {
            System.out.println("⚠️ No questions found for Role: " + role + " and Category: " + category);
        } else {
            System.out.println("✅ Questions retrieved: " + questions.size());
            for (Question q : questions) {
                System.out.println("   - " + q.getText());  // Display question text
            }
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
    public String createCustomQuestionnaire(@RequestParam List<Long> selectedQuestions, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserQuestionnaire userQuestionnaire = userQuestionnaireService.createUserQuestionnaire(username, selectedQuestions);
        model.addAttribute("questions", userQuestionnaire.getSelectedQuestions());
        model.addAttribute("custom", true); // Helps in UI to differentiate custom questionnaire

        return "questionnaire";
    }
}

