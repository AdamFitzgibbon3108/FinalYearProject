package com.example.controller;

import com.example.model.SurveyQuestion;
import com.example.model.SurveyResponse;
import com.example.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller  // ✅ Must be @Controller to return a Thymeleaf page
@RequestMapping("/survey")
public class SurveyController {

    private static final Logger logger = Logger.getLogger(SurveyController.class.getName());

    @Autowired
    private SurveyService surveyService;

    // ✅ Correct mapping for the survey page
    @GetMapping("/page")  
    public String showSurveyPage(Model model) {
        logger.info("Rendering survey.html page...");
        List<SurveyQuestion> questions = surveyService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "survey";  // ✅ This must match `survey.html` in templates/
    }

    // ✅ API to get survey questions (for frontend JavaScript)
    @GetMapping("/questions")
    @ResponseBody
    public List<SurveyQuestion> getSurveyQuestions() {
        logger.info("Fetching all survey questions...");
        List<SurveyQuestion> questions = surveyService.getAllQuestions();
        logger.info("Total questions retrieved: " + questions.size());
        return questions;
    }

    // ✅ API to submit survey responses
    @PostMapping("/submit")
    @ResponseBody
    public Map<String, String> submitSurveyResponses(@RequestBody List<SurveyResponse> responses) {
        logger.info("Received survey responses: " + responses.size());

        if (responses.isEmpty()) {
            logger.warning("No responses received.");
            return Map.of("error", "No survey responses provided.");
        }

        surveyService.saveSurveyResponses(responses);
        logger.info("Survey responses saved successfully.");

        String recommendedCategory = surveyService.analyzeResponses(responses);
        logger.info("Recommended Security Category: " + recommendedCategory);

        return Map.of("recommendedCategory", recommendedCategory);
    }
}
