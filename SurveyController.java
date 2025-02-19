package com.example.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.SurveyQuestion;
import com.example.model.SurveyResponse;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.SurveyService;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Renders survey.html when accessing /survey-page
    @GetMapping("/page")
    public String showSurveyPage(Model model) {
        List<SurveyQuestion> questions = surveyService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "survey"; // ✅ Loads survey.html
    }

    // ✅ API to get all survey questions (returns JSON)
    @GetMapping
    @ResponseBody
    public List<SurveyQuestion> getSurveyQuestions() {
        return surveyService.getAllQuestions();
    }

    // ✅ API to submit survey responses
    @PostMapping("/submit")
    @ResponseBody
    public String submitSurveyResponses(@RequestBody List<SurveyResponse> responses, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        surveyService.saveSurveyResponses(userId, responses);

        String recommendedCategory = surveyService.analyzeResponses(user, responses);

        return "Survey submitted successfully. Recommended category: " + recommendedCategory;
    }
}


