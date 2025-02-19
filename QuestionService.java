package com.example.service;

import com.example.model.Question;
import com.example.model.QuestionType;
import com.example.model.SecurityControl;
import com.example.repository.QuestionRepository;
import com.example.repository.SecurityControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SecurityControlRepository securityControlRepository;

    /**
     * Fetches all questions from the database.
     */
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Fetches questions by role and difficulty.
     * Ensures questions are randomly shuffled for variety.
     */
    public List<Map<String, Object>> getQuestionsForRole(String role, String difficulty) {
        List<Map<String, Object>> questions = new ArrayList<>();
        List<Question> securityQuestions = questionRepository.findByRoleAndDifficulty(role, difficulty);

        for (Question sq : securityQuestions) {
            questions.add(convertQuestionToMap(sq));
        }

        Collections.shuffle(questions);
        return questions;
    }

    /**
     * Fetches questions by role and category.
     */
    public List<Question> getQuestionsByRoleAndCategory(String role, String category) {
        return questionRepository.findByRoleAndCategory(role, category);
    }

    /**
     * Fetches questions by category.
     */
    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }
    
    
    /**
     * Fetches questions by role only.
     */  
    public List<Question> getQuestionsByRole(String role) {
        return questionRepository.findByRole(role);
    }

    

    /**
     * Fetches all questions belonging to a given security control category.
     */
    public List<Question> getQuestionsByControlCategory(String categoryName) {
        SecurityControl controlCategory = securityControlRepository.findByName(categoryName);
        if (controlCategory == null) {
            return new ArrayList<>(); // Return empty list if category doesn't exist
        }
        return questionRepository.findByControlCategory(controlCategory);
    }

    /**
     * Fetches a single question by ID.
     */
    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    /**
     * Fetches all unique roles from the database.
     */
    public List<String> getAllRoles() {
        return questionRepository.findDistinctRoles();
    }

    /**
     * Fetches all unique categories from the database.
     */
    public List<String> getAllCategories() {
        return questionRepository.findDistinctCategories();
    }

    /**
     * Converts a Question entity into a structured Map.
     */
    private Map<String, Object> convertQuestionToMap(Question question) {
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("id", question.getId());
        questionData.put("question", question.getText());
        questionData.put("questionType", question.getQuestionType().toString());
        questionData.put("category", question.getCategory());
        questionData.put("framework", question.getFramework());
        questionData.put("difficulty", question.getDifficulty());
        questionData.put("score", question.getScore());
        questionData.put("role", question.getRole());

        // Ensure control category is not null before accessing
        if (question.getControlCategory() != null) {
            questionData.put("controlCategory", question.getControlCategory().getName());
        } else {
            questionData.put("controlCategory", "Unknown");
        }

        // Include correct answer for internal validation (not exposed to user)
        questionData.put("correctAnswer", question.getCorrectAnswer());

        // Handle question options
        if (question.getQuestionType() == QuestionType.TRUE_FALSE) {
            questionData.put("options", List.of("True", "False"));
        } else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            List<String> options = extractOptions(question);
            if (!options.isEmpty()) {
                questionData.put("options", options);
            }
        }

        return questionData;
    }

    /**
     * Extracts multiple-choice options from the database safely.
     * Assumes options are stored in a separate column, formatted as "option1,option2,option3,option4".
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
