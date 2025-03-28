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

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsForRole(String role, String difficulty) {
        List<Question> securityQuestions = questionRepository.findByRoleAndDifficulty(role, difficulty);
        Collections.shuffle(securityQuestions);
        return securityQuestions;
    }

    public List<Question> getQuestionsByRoleAndCategory(String role, String category) {
        return questionRepository.findByRoleAndCategory(role, category);
    }

    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

    public List<Question> getQuestionsByRole(String role) {
        return questionRepository.findByRole(role);
    }

    public List<Question> getQuestionsByControlCategory(String categoryName) {
        SecurityControl controlCategory = securityControlRepository.findByName(categoryName);
        if (controlCategory == null) {
            System.out.println("⚠️ Warning: Security control category '" + categoryName + "' not found.");
            return Collections.emptyList();
        }
        return questionRepository.findByControlCategory(controlCategory);
    }

    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Question> getQuestionsByIds(List<Long> questionIds) {
        List<Question> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            System.out.println("⚠️ Warning: Some selected question IDs were not found in the database.");
        }
        return questions;
    }

    public List<String> getAllRoles() {
        return questionRepository.findDistinctRoles();
    }

    public List<String> getAllCategories() {
        return questionRepository.findDistinctCategories();
    }

    public Map<String, List<String>> getGroupedSecurityControls() {
        List<SecurityControl> allControls = securityControlRepository.findAll();
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (SecurityControl control : allControls) {
            String group = control.getCategoryGroup() != null ? control.getCategoryGroup() : "Uncategorized";
            grouped.putIfAbsent(group, new ArrayList<>());
            grouped.get(group).add(control.getName());
        }

        return grouped;
    }

    public List<Question> searchByKeyword(String keyword) {
        return questionRepository.findByQuestionTextContainingIgnoreCase(keyword);
    }

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

        if (question.getControlCategory() != null) {
            questionData.put("controlCategory", question.getControlCategory().getName());
        } else {
            questionData.put("controlCategory", "Unknown");
        }

        questionData.put("correctAnswer", question.getCorrectAnswer());

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

    private List<String> extractOptions(Question question) {
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            String optionsStr = question.getOptions();
            if (optionsStr != null && !optionsStr.trim().isEmpty()) {
                return Arrays.asList(optionsStr.split(","));
            }
        }
        return Collections.emptyList();
    }
}
