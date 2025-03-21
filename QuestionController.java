package com.example.controller;

import com.example.model.Question;
import com.example.model.User;
import com.example.model.Response;
import com.example.service.QuestionService;
import com.example.repository.QuestionRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller  // ✅ Ensure it's a Thymeleaf-compatible controller
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
     * Load Manage Questions Page
     */
    @GetMapping("/manage")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        return "manage-questions"; // ✅ Ensure `manage-questions.html` exists in `templates/`
    }

    /**
     * Fetch all unique roles from the database.
     */
    @GetMapping("/roles")
    @ResponseBody
    public List<String> getAllRoles() {
        return questionService.getAllRoles();
    }
    
    @GetMapping("/questionnaire/custom")
    public String customQuestionnaire(Model model) {
        List<Question> allQuestions = questionService.getAllQuestions(); // Fetch all questions
        model.addAttribute("questions", allQuestions); // Pass questions to view
        return "custom-questionnaire"; // Make sure 'custom-questionnaire.html' exists in templates/
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

        return "questionnaire"; // ✅ Ensure `questionnaire.html` exists in `templates/`
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

        return "questionnaire"; // ✅ Ensure `questionnaire.html` exists in `templates/`
    }

    /**
     * Handles questionnaire submission.
     */
    @Transactional
    @PostMapping("/submit")
    @ResponseBody
    public Map<String, String> submitQuestionnaire(@RequestBody Map<String, String> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        System.out.println("🔍 Received submission from user: " + username);

        // Retrieve user
        Optional<User> userOptional = userRepository.findByUsername(username);
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

        System.out.println("📌 Role: " + selectedRole + ", Category: " + selectedCategory);

        // Process user responses
        List<Response> savedResponses = new ArrayList<>();

        for (Map.Entry<String, String> entry : requestBody.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                    String answer = entry.getValue();

                    Optional<Question> questionOptional = questionRepository.findById(questionId);
                    if (questionOptional.isPresent()) {
                        Question question = questionOptional.get();

                        // Ensure category is properly set
                        String category = (selectedCategory == null || selectedCategory.isEmpty()) ? question.getCategory() : selectedCategory;

                        Response response = new Response();
                        response.setUser(user);
                        response.setQuestion(question);
                        response.setAnswer(answer);
                        response.setTimestamp(LocalDateTime.now());
                        response.setRole(selectedRole);
                        response.setCategory(category); 
                        response.setScore(0); // Placeholder for scoring

                        try {
                            responseRepository.save(response);
                            savedResponses.add(response);
                            System.out.println("✅ Saved response for question " + questionId);
                        } catch (Exception e) {
                            System.out.println("❌ Error saving response: " + e.getMessage());
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("⚠️ Warning: Question ID " + questionId + " not found.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error parsing question ID: " + entry.getKey());
                }
            }
        }

        // Debugging: Fetch responses from the DB to verify if they were saved
        List<Response> allResponses = responseRepository.findByUser(user);
        System.out.println("🔍 Total Responses in DB for user: " + allResponses.size());

        return Collections.singletonMap("message", "Responses submitted successfully!");
    }
}
