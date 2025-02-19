package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SurveyService {

    @Autowired
    private SurveyQuestionRepository questionRepository;

    @Autowired
    private SurveyResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    // Fetch all survey questions
    public List<SurveyQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Process user responses and determine recommended security category
    public String analyzeResponses(User user, List<SurveyResponse> responses) {
        Map<String, Integer> categoryScores = new HashMap<>();

        for (SurveyResponse response : responses) {
            String answer = response.getResponse();
            categoryScores.put(answer, categoryScores.getOrDefault(answer, 0) + 1);
        }

        // Determine the highest-scored security category
        String recommendedCategory = categoryScores.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .get()
            .getKey();

        // Save the recommendation to the user profile
        user.setSurveyCompleted(true);
        user.setRecommendedSecurityCategory(recommendedCategory);
        userRepository.save(user);

        return recommendedCategory;
    }

 // Save survey responses properly linked to a user
    public void saveSurveyResponses(Long userId, List<SurveyResponse> responses) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (SurveyResponse response : responses) {
            response.setUser(user); // ✅ Link each response to the user
        }

        responseRepository.saveAll(responses);
    }
}
    

