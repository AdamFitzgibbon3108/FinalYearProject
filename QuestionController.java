package com.example.controller;

import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Endpoint to fetch questions based on user role.
     *
     * @param role The role of the user (e.g., "employee", "admin").
     * @return A list of dynamically generated questions tailored to the role.
     */
    @GetMapping("/questions/{role}")
    public List<Map<String, Object>> getQuestionsByRole(@PathVariable String role) {
        try {
            // Fetch and return questions as a list of maps with question text, options, and correct answer.
            return questionService.getQuestionsForRole(role);
        } catch (Exception e) {
            // Handle exceptions and provide an appropriate response
            e.printStackTrace();
            return List.of(Map.of("error", "Error fetching questions. Please try again later."));
        }
    }
}
