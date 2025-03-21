package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class SurveyService {

    private static final Logger logger = Logger.getLogger(SurveyService.class.getName());

    @Autowired
    private SurveyQuestionRepository questionRepository;

    @Autowired
    private SurveyResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyQuestionOptionsRepository optionsRepository;

    private final Map<String, List<String>> userRecommendations = new HashMap<>();

    /**
     * Fetch all survey questions.
     */
    public List<SurveyQuestion> getAllQuestions() {
        logger.info("Retrieving all survey questions...");
        return questionRepository.findAll();
    }

    /**
     * Save survey responses for a user.
     */
    public void saveSurveyResponses(List<SurveyResponse> responses, String username) {
        logger.info("Saving survey responses for user: " + username);

        // Retrieve User
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            logger.severe("User not found: " + username);
            throw new RuntimeException("User not found: " + username);
        }
        User user = userOptional.get();

        List<SurveyResponse> validResponses = new ArrayList<>();

        for (SurveyResponse response : responses) {
            if (response.getSurveyQuestion() == null || response.getSurveyQuestion().getId() == null) {
                logger.warning("Skipping response: Missing Survey Question ID");
                continue;
            }

            // Ensure question exists
            Optional<SurveyQuestion> questionOpt = questionRepository.findById(response.getSurveyQuestion().getId());
            if (questionOpt.isEmpty()) {
                logger.warning("Skipping response: Invalid Survey Question ID " + response.getSurveyQuestion().getId());
                continue;
            }
            response.setSurveyQuestion(questionOpt.get());

            // Ensure selected option exists
            if (response.getSelectedOption() == null || response.getSelectedOption().getId() == null) {
                logger.warning("Skipping response: Missing Selected Option");
                continue;
            }

            Optional<SurveyQuestionOptions> optionOpt = optionsRepository.findById(response.getSelectedOption().getId());
            if (optionOpt.isEmpty()) {
                logger.warning("Skipping response: Invalid Option ID " + response.getSelectedOption().getId());
                continue;
            }
            response.setSelectedOption(optionOpt.get());

            response.setUser(user);
            validResponses.add(response);
        }

        // Save only valid responses
        if (!validResponses.isEmpty()) {
            responseRepository.saveAll(validResponses);
            logger.info("Survey responses saved successfully for user: " + username);
        } else {
            logger.warning("No valid survey responses to save.");
        }
    }

    /**
     * Analyze responses and determine recommended security categories.
     */
    public List<String> analyzeResponses(String username) {
        logger.info("Analyzing responses for user: " + username);

        List<SurveyResponse> responses = responseRepository.findByUser_Username(username);
        if (responses.isEmpty()) {
            logger.warning("No responses found for user: " + username);
            return List.of("General Security Awareness");
        }

        Map<String, Integer> categoryScores = new HashMap<>();
        for (SurveyResponse response : responses) {
            String answer = response.getSelectedOption().getOptionValue().toLowerCase();
            Set<String> categories = mapResponseToCategories(answer);
            for (String category : categories) {
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 1);
            }
        }

        List<String> recommendations = getTopCategories(categoryScores);
        logger.info("Generated recommendations for user: " + username + " -> " + recommendations);

        //  Store multiple recommendations as a comma-separated string in the database
        updateUserRecommendations(username, recommendations);

        return recommendations;
    }

    /**
     *  Store multiple recommended security categories in the database.
     */
    public void updateUserRecommendations(String username, List<String> categories) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String recommendationsString = String.join(",", categories);
            user.setRecommendedSecurityCategory(recommendationsString);
            userRepository.save(user);
            logger.info("Updated recommended security categories for user: " + username + " -> " + recommendationsString);
        } else {
            logger.warning("Failed to update recommendations: User not found - " + username);
        }
    }

    /**
     *  Retrieve stored recommendations from the database (multiple categories supported).
     */
    public List<String> getStoredUserRecommendations(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            String storedCategories = userOptional.get().getRecommendedSecurityCategory();
            if (storedCategories != null && !storedCategories.isBlank()) {
                return Arrays.asList(storedCategories.split(","));
            }
        }
        return List.of("No recommendation available");
    }

    /**
     * Maps responses to security categories.
     */
    private Set<String> mapResponseToCategories(String response) {
        Map<String, List<String>> categoryMap = new HashMap<>();
        categoryMap.put("Network Security", List.of("firewall", "ddos", "network", "intrusion detection"));
        categoryMap.put("Privacy", List.of("vpn", "data privacy", "gdpr", "tracking"));
        categoryMap.put("Authentication", List.of("2fa", "password", "mfa", "otp", "biometric"));
        categoryMap.put("Secure Development", List.of("secure coding", "owasp", "software security"));
        categoryMap.put("Cryptography", List.of("encryption", "hashing", "rsa", "aes", "secure communication"));
        categoryMap.put("Malware Analysis", List.of("malware", "virus", "trojan", "ransomware"));
        categoryMap.put("Incident Response", List.of("security breach", "forensics", "breach response"));
        categoryMap.put("Penetration Testing", List.of("pen testing", "ethical hacking", "security audit"));
        categoryMap.put("Web Security", List.of("sql injection", "xss", "csrf", "clickjacking"));
        categoryMap.put("Social Engineering", List.of("phishing", "scam", "impersonation"));
        categoryMap.put("Threat Intelligence", List.of("zero-day", "cyber espionage", "threat detection"));
        categoryMap.put("Security Awareness", List.of("security awareness", "employee training", "best practices"));

        Set<String> categories = new HashSet<>();
        for (var entry : categoryMap.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (response.contains(keyword)) {
                    categories.add(entry.getKey());
                }
            }
        }

        return categories.isEmpty() ? Set.of("General Security Awareness") : categories;
    }

    /**
     * Returns the top 3 scoring security categories.
     */
    private List<String> getTopCategories(Map<String, Integer> categoryScores) {
        return categoryScores.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }
}




