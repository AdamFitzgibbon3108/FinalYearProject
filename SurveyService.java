package com.example.service;

import com.example.model.SurveyQuestion;
import com.example.model.SurveyResponse;
import com.example.repository.SurveyQuestionRepository;
import com.example.repository.SurveyResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

@Service
public class SurveyService {

    private static final Logger logger = Logger.getLogger(SurveyService.class.getName());

    @Autowired
    private SurveyQuestionRepository questionRepository;

    @Autowired
    private SurveyResponseRepository responseRepository;

    // Fetch all survey questions
    public List<SurveyQuestion> getAllQuestions() {
        logger.info("Retrieving all survey questions...");
        return questionRepository.findAll();
    }

    // Process user responses and determine the recommended security category
    public String analyzeResponses(List<SurveyResponse> responses) {
        logger.info("Analyzing responses to determine recommended category...");

        if (responses.isEmpty()) {
            logger.warning("No responses provided for analysis.");
            return "General Security Awareness";
        }

        Map<String, Integer> categoryScores = new HashMap<>();
        for (SurveyResponse response : responses) {
            if (response.getResponse() == null || response.getResponse().trim().isEmpty()) {
                logger.warning("Empty response detected, skipping...");
                continue;
            }

            String answer = response.getResponse().toLowerCase();
            String category = mapResponseToCategory(answer);

            logger.info("Mapped response [" + answer + "] to category: " + category);
            categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 1);
        }

        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("General Security Awareness");
    }

    // Save survey responses
    public void saveSurveyResponses(List<SurveyResponse> responses) {
        logger.info("Saving survey responses...");
        if (responses.isEmpty()) {
            logger.warning("Attempted to save empty responses.");
            return;
        }
        responseRepository.saveAll(responses);
        logger.info("Survey responses saved successfully.");
    }

    // Maps responses to specific security categories
    private String mapResponseToCategory(String response) {
        Map<String, String> categoryMap = new HashMap<>();
        categoryMap.put("firewalls", "Network Security");
        categoryMap.put("vpn", "Privacy");
        categoryMap.put("two-factor authentication", "Authentication");
        categoryMap.put("mfa", "Authentication");
        categoryMap.put("password manager", "Secure Development");
        categoryMap.put("data encryption", "Cryptography");
        categoryMap.put("malware protection", "Malware Analysis");
        categoryMap.put("incident response", "Incident Response");
        categoryMap.put("penetration testing", "Penetration Testing");
        categoryMap.put("web security", "Web Security");
        categoryMap.put("phishing", "Social Engineering");
        categoryMap.put("ransomware", "Threat Detection");
        categoryMap.put("security awareness", "Security Awareness");

        return categoryMap.getOrDefault(response, "General Security Awareness");
    }
}


