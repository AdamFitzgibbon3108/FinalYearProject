package com.example.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Question;
import com.example.model.QuestionType;
import com.example.model.SecurityControl;
import com.example.repository.QuestionRepository;
import com.example.repository.SecurityControlRepository;

@Service
public class QuestionService {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private SecurityControlRepository securityControlRepository;

	// Fetch all questions
	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	// Fetch questions for a role + difficulty
	public List<Question> getQuestionsForRole(String role, String difficulty) {
		List<Question> securityQuestions = questionRepository.findByRoleAndDifficulty(role, difficulty);
		Collections.shuffle(securityQuestions);
		return securityQuestions;
	}

	// Fetch questions by role and category (but pick max 15 random)
	public List<Question> getQuestionsByRoleAndCategory(String role, String category) {
		List<Question> questions = questionRepository.findByRoleAndCategory(role, category);
		Collections.shuffle(questions);
		return questions.size() > 15 ? questions.subList(0, 15) : questions;
	}

	// Fetch questions by category only
	public List<Question> getQuestionsByCategory(String category) {
		return questionRepository.findByCategory(category);
	}

	// Fetch questions by role only (but pick max 15 random)
	public List<Question> getQuestionsByRole(String role) {
		List<Question> questions = questionRepository.findByRole(role);
		Collections.shuffle(questions);
		return questions.size() > 15 ? questions.subList(0, 15) : questions;
	}

	// Fetch questions by linked security control category name
	public List<Question> getQuestionsByControlCategory(String categoryName) {
		SecurityControl controlCategory = securityControlRepository.findByName(categoryName);
		if (controlCategory == null) {
			System.out.println("⚠️ Warning: Security control category '" + categoryName + "' not found.");
			return Collections.emptyList();
		}
		return questionRepository.findByControlCategory(controlCategory);
	}

	// Find single question by ID
	public Optional<Question> getQuestionById(Long questionId) {
		return questionRepository.findById(questionId);
	}

	// Fetch multiple questions by IDs
	public List<Question> getQuestionsByIds(List<Long> questionIds) {
		List<Question> questions = questionRepository.findAllById(questionIds);
		if (questions.size() != questionIds.size()) {
			System.out.println("⚠️ Warning: Some selected question IDs were not found in the database.");
		}
		return questions;
	}

	// Fetch all distinct roles
	public List<String> getAllRoles() {
		return questionRepository.findDistinctRoles();
	}

	// Fetch all distinct categories
	public List<String> getAllCategories() {
		return questionRepository.findDistinctCategories();
	}

	// Group Security Controls by Category Group
	public Map<String, List<String>> getGroupedSecurityControls() {
		List<SecurityControl> allControls = securityControlRepository.findAll();
		Map<String, List<String>> grouped = new LinkedHashMap<>();

		for (SecurityControl control : allControls) {
			String group = control.getCategoryGroup() != null ? control.getCategoryGroup() : "Uncategorized";
			grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(control.getName());
		}

		return grouped;
	}

	// Search questions by keyword inside question text
	public List<Question> searchByKeyword(String keyword) {
		return questionRepository.findByQuestionTextContainingIgnoreCase(keyword);
	}

	// Convert a question to a custom map (used for APIs or JSON if needed)
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
		questionData.put("correctAnswer", question.getCorrectAnswer());

		if (question.getControlCategory() != null) {
			questionData.put("controlCategory", question.getControlCategory().getName());
		} else {
			questionData.put("controlCategory", "Unknown");
		}

		if (question.getQuestionType() == QuestionType.TRUE_FALSE) {
			questionData.put("options", List.of("True", "False"));
		} else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
			List<String> options = extractOptions(question);
			questionData.put("options",
					options.isEmpty() ? List.of("Option A", "Option B", "Option C", "Option D") : options);
		}

		return questionData;
	}

	// Extract options from a question (split comma-separated values)
	private List<String> extractOptions(Question question) {
		if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
			String optionsStr = question.getOptions();
			if (optionsStr != null && !optionsStr.trim().isEmpty()) {
				return Arrays.stream(optionsStr.split(",")).map(String::trim).filter(opt -> !opt.isEmpty()).toList();
			}
		}
		return Collections.emptyList();
	}

	// Check if a question already exists by text
	public boolean questionExistsByText(String text) {
		return questionRepository.existsByTextIgnoreCase(text.trim());
	}
}
