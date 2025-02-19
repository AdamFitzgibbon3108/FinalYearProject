package com.example.controller;

import com.example.model.Question;
import com.example.model.QuestionType;
import com.example.model.Response;
import com.example.model.User;
import com.example.service.QuestionService;
import com.example.repository.QuestionRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller  // ✅ Changed from @RestController to handle Thymeleaf views
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;

    /**
     * Fetch all unique roles from the database.
     */
    @GetMapping("/roles")
    @ResponseBody
    public List<String> getAllRoles() {
        return questionService.getAllRoles();
    }

    /**
     * Fetch all unique categories from the database.
     */
    @GetMapping("/categories")
    @ResponseBody
    public List<String> getAllCategories() {
        return questionService.getAllCategories();
    }

    /**
     * Fetch all questions for a given category.
     */
    @GetMapping("/category/{category}")
    @ResponseBody
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    /**
     * Load questionnaire based on role only.
     */
    @GetMapping("/start-role-only")
    public String startRoleBasedQuestionnaire(@RequestParam("selectedRole") String selectedRole, Model model) {
        List<Question> questions = questionService.getQuestionsByRole(selectedRole);

        // Pass data to the Thymeleaf template
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", "None");

        return "questionnaire"; // ✅ Ensure there's a `questionnaire.html` template in `templates/`
    }

    /**
     * Load questionnaire based on role and category.
     */
    @GetMapping("/start")
    public String startQuestionnaire(@RequestParam("selectedRole") String selectedRole,
                                     @RequestParam(value = "selectedCategory", required = false) String selectedCategory,
                                     Model model) {
        List<Question> questions = questionService.getQuestionsByRoleAndCategory(selectedRole, selectedCategory);

        // Pass data to the Thymeleaf template
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", selectedCategory);

        return "questionnaire"; // ✅ Ensure `questionnaire.html` exists in Thymeleaf templates
    }

    /**
     * Handles questionnaire submission.
     */
    @PostMapping("/submit")
    @ResponseBody
    public Map<String, String> submitQuestionnaire(@RequestBody Map<String, String> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Retrieve user
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isEmpty()) {
            return Collections.singletonMap("error", "User not found!");
        }
        User user = userOptional.get();

        // Retrieve role and category from request body
        String selectedRole = requestBody.get("selectedRole");
        String selectedCategory = requestBody.get("selectedCategory");

        if (selectedRole == null || selectedCategory == null) {
            return Collections.singletonMap("error", "Missing role or category.");
        }

        // Process user responses
        List<Response> savedResponses = new ArrayList<>();

        for (Map.Entry<String, String> entry : requestBody.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                    String answer = entry.getValue();

                    Optional<Question> questionOptional = questionRepository.findById(questionId);
                    if (questionOptional.isPresent()) {
                        Response response = new Response();
                        response.setUser(user);
                        response.setQuestion(questionOptional.get());
                        response.setAnswer(answer);
                        response.setTimestamp(LocalDateTime.now());
                        response.setRole(selectedRole);
                        response.setDifficulty(selectedCategory);
                        response.setScore(0); // Placeholder, update later when scoring logic is added

                        responseRepository.save(response);
                        savedResponses.add(response);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Could not parse question ID: " + entry.getKey());
                }
            }
        }

        return Collections.singletonMap("message", "Responses submitted successfully!");
    }

    /**
     * Extracts multiple-choice options from the database safely.
     */
    private List<String> extractOptions(Question question) {
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            String optionsStr = question.getOptions();
            if (optionsStr != null && !optionsStr.isEmpty()) {
                return Arrays.asList(optionsStr.split(","));
            }
        }
        return Collections.emptyList();
    }
}
