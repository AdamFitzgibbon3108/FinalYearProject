package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    private static final String TRIVIA_API_URL = "https://opentdb.com/api.php?amount=5&category=18&type=multiple";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QuestionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<Map<String, Object>> getQuestionsForRole(String role) {
        List<Map<String, Object>> questions = new ArrayList<>();

        try {
            // Debugging: Log the role
            System.out.println("Fetching questions for role: " + role);

            // Fetch questions from the Trivia API
            String response = restTemplate.getForObject(TRIVIA_API_URL, String.class);

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);

                // Extract the results array
                JsonNode results = jsonNode.get("results");
                if (results == null || results.isEmpty()) {
                    System.out.println("No results found in the API response.");
                    return questions;
                }

                // Parse each question
                for (JsonNode result : results) {
                    Map<String, Object> questionData = new HashMap<>();
                    questionData.put("question", result.get("question").asText());

                    // Combine incorrect answers and correct answer into options
                    List<String> options = new ArrayList<>();
                    result.get("incorrect_answers").forEach(answer -> options.add(answer.asText()));
                    options.add(result.get("correct_answer").asText());

                    // Shuffle options to randomize their order
                    java.util.Collections.shuffle(options);

                    questionData.put("options", options);
                    questionData.put("correctAnswer", result.get("correct_answer").asText());

                    questions.add(questionData);
                }
            } else {
                System.out.println("Trivia API returned no response.");
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch questions from the Trivia API: " + e.getMessage());
            e.printStackTrace();
        }

        return questions;
    }
}
