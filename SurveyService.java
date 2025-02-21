package com.example.service;

import com.example.model.SurveyQuestion;
import com.example.model.SurveyResponse;
import com.example.repository.SurveyQuestionRepository;
import com.example.repository.SurveyResponseRepository;
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

    private final Map<String, String> userRecommendations = new HashMap<>(); // Temporary storage for recommendations

    // Fetch all survey questions
    public List<SurveyQuestion> getAllQuestions() {
        logger.info("Retrieving all survey questions...");
        return questionRepository.findAll();
    }

    // Process user responses and determine the recommended security category
    public String analyzeResponses(List<SurveyResponse> responses, String username) {
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
            Set<String> matchedCategories = mapResponseToCategories(answer);

            for (String category : matchedCategories) {
                logger.info("Mapped response [" + answer + "] to category: " + category);
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 1);
            }
        }

        String recommendedCategory = categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("General Security Awareness");

        // Store recommendation in memory (replace with DB if needed)
        userRecommendations.put(username, recommendedCategory);

        logger.info("Final recommended category: " + recommendedCategory);
        return recommendedCategory;
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

    // Retrieve stored recommendation for a user
    public String getUserRecommendation(String username) {
        return userRecommendations.getOrDefault(username, "No recommendation available");
    }

    // Maps responses to specific security categories (supports partial matching)
    private Set<String> mapResponseToCategories(String response) {
        Set<String> categories = new HashSet<>();
        response = response.toLowerCase(); // ✅ Standardize case

        // ✅ Define categories with broader keywords & synonyms
        Map<String, List<String>> categoryMap = new HashMap<>();

        categoryMap.put("Network Security", Arrays.asList("firewall", "ddos", "network", "packet sniffing", "intrusion"));
        categoryMap.put("Privacy", Arrays.asList("vpn", "data privacy", "gdpr", "ccpa", "anonymous", "tracking"));
        categoryMap.put("Authentication", Arrays.asList("2fa", "mfa", "otp", "password reset", "hacked login", "biometric", "social login"));
        categoryMap.put("Secure Development", Arrays.asList("password manager", "secure coding", "owasp", "software security", "secure app"));
        categoryMap.put("Cryptography", Arrays.asList("encryption", "hashing", "crypto", "rsa", "aes", "secure communication"));
        categoryMap.put("Malware Analysis", Arrays.asList("malware", "virus", "trojan", "ransomware", "spyware", "rootkit"));
        categoryMap.put("Incident Response", Arrays.asList("security breach", "forensics", "breach response", "incident report"));
        categoryMap.put("Penetration Testing", Arrays.asList("pen testing", "ethical hacking", "security audit", "reconnaissance"));
        categoryMap.put("Web Security", Arrays.asList("sql injection", "xss", "csrf", "clickjacking", "website security", "web hacking"));
        categoryMap.put("Social Engineering", Arrays.asList("phishing", "scam", "vishing", "impersonation", "tailgating"));
        categoryMap.put("Threat Intelligence", Arrays.asList("zero-day", "apt", "cyber espionage", "threat detection"));
        categoryMap.put("Security Awareness", Arrays.asList("security awareness", "employee training", "security best practices"));
        categoryMap.put("Identity Security", Arrays.asList("identity theft", "fraud", "impersonation", "fake identity", "stolen id"));

        // ✅ Check if response contains any relevant words
        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (response.contains(keyword)) {
                    categories.add(entry.getKey());
                }
            }
        }

        return categories.isEmpty() ? Collections.singleton("General Security Awareness") : categories;
    }
}
