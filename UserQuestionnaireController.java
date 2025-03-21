package com.example.controller;

import com.example.model.User;
import com.example.model.UserQuestionnaire;
import com.example.service.UserQuestionnaireService;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-questionnaires")
public class UserQuestionnaireController {

    @Autowired
    private UserQuestionnaireService userQuestionnaireService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new questionnaire for the authenticated user.
     */
    @PostMapping("/create")
    public UserQuestionnaire createUserQuestionnaire(@RequestParam(required = false) List<Long> selectedQuestions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        //   Properly retrieve the user from Optional
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        // Ensure selectedQuestions is not null (avoid NullPointerException)
        if (selectedQuestions == null) {
            selectedQuestions = Collections.emptyList();
        }
        
        return userQuestionnaireService.createUserQuestionnaire(username, selectedQuestions);
    }

    /**
     * Add a question to a user's questionnaire.
     */
    @PostMapping("/{questionnaireId}/add-question/{questionId}")
    public String addQuestionToQuestionnaire(@PathVariable Long questionnaireId, @PathVariable Long questionId) {
        try {
            userQuestionnaireService.addQuestionToUserQuestionnaire(questionnaireId, questionId);
            return "{ \"message\": \"Question added successfully!\" }";
        } catch (Exception e) {
            return "{ \"error\": \"Failed to add question: " + e.getMessage() + "\" }";
        }
    }

    /**
     * Remove a question from a user's questionnaire.
     */
    @DeleteMapping("/{questionnaireId}/remove-question/{questionId}")
    public String removeQuestionFromQuestionnaire(@PathVariable Long questionnaireId, @PathVariable Long questionId) {
        try {
            userQuestionnaireService.removeQuestionFromUserQuestionnaire(questionnaireId, questionId);
            return "{ \"message\": \"Question removed successfully!\" }";
        } catch (Exception e) {
            return "{ \"error\": \"Failed to remove question: " + e.getMessage() + "\" }";
        }
    }

    /**
     * Retrieve all questionnaires for the authenticated user.
     */
    @GetMapping
    public List<UserQuestionnaire> getUserQuestionnaires() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userQuestionnaireService.getUserQuestionnaires(username);
    }

    /**
     * Retrieve a specific questionnaire by ID.
     */
    @GetMapping("/{questionnaireId}")
    public Optional<UserQuestionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
        return userQuestionnaireService.getUserQuestionnaireSummary(questionnaireId);
    }
}
