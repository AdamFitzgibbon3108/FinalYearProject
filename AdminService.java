package com.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.Role;
import com.example.model.SecurityControl;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.QuestionnaireRepository;
import com.example.repository.QuizResultRepository;
import com.example.repository.RoleRepository;
import com.example.repository.SecurityControlRepository;
import com.example.repository.UserRepository;

@Service
public class AdminService {

	@Autowired
	private QuizResultRepository quizResultRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionnaireRepository questionnaireRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private SecurityControlRepository securityControlRepository;

	// Get all questions
	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	// Get all questionnaires
	public List<Questionnaire> getAllQuestionnaires() {
		return questionnaireRepository.findAll();
	}

	// Get a specific questionnaire by ID
	public Questionnaire getQuestionnaireById(Long questionnaireId) {
		return questionnaireRepository.findById(questionnaireId)
				.orElseThrow(() -> new RuntimeException("Questionnaire not found with ID: " + questionnaireId));
	}

	// Create a new questionnaire
	@Transactional
	public Questionnaire createQuestionnaire(String title, String description, String adminUsername) {
		User adminUser = userRepository.findByUsername(adminUsername)
				.orElseThrow(() -> new RuntimeException("Admin user not found"));

		if (!adminUser.hasRole("ADMIN")) {
			throw new RuntimeException("User is not an admin.");
		}

		Questionnaire questionnaire = new Questionnaire(title, description, adminUser, null);
		return questionnaireRepository.save(questionnaire);
	}

	// Assign questions to a questionnaire
	@Transactional
	public Questionnaire assignQuestionsToQuestionnaire(Long questionnaireId, List<Long> questionIds) {
		Questionnaire questionnaire = getQuestionnaireById(questionnaireId);
		List<Question> selectedQuestions = questionRepository.findAllById(questionIds);
		questionnaire.getQuestions().addAll(selectedQuestions);
		return questionnaireRepository.save(questionnaire);
	}

	// Remove questions from a questionnaire
	@Transactional
	public Questionnaire removeQuestionsFromQuestionnaire(Long questionnaireId, List<Long> questionIds) {
		Questionnaire questionnaire = getQuestionnaireById(questionnaireId);
		questionnaire.getQuestions().removeIf(q -> questionIds.contains(q.getId()));
		return questionnaireRepository.save(questionnaire);
	}

	// Delete a questionnaire
	@Transactional
	public void deleteQuestionnaire(Long questionnaireId) {
		if (questionnaireRepository.existsById(questionnaireId)) {
			questionnaireRepository.deleteById(questionnaireId);
		} else {
			throw new RuntimeException("Questionnaire not found with ID: " + questionnaireId);
		}
	}

	// Get all users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Assign a role to a user
	@Transactional
	public void assignRoleToUser(Long userId, String roleName) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

		if (!user.getRoles().contains(role)) {
			user.addRole(role);
			userRepository.save(user);
		}
	}

	// Remove a role from a user
	@Transactional
	public void removeRoleFromUser(Long userId, String roleName) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		user.getRoles().removeIf(role -> role.getName().equals(roleName));
		userRepository.save(user);
	}

	// Update user details
	@Transactional
	public User updateUser(Long userId, Map<String, Object> updates) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (updates.containsKey("username")) {
			String newUsername = (String) updates.get("username");
			if (newUsername != null && !newUsername.isEmpty()) {
				user.setUsername(newUsername);
			}
		}

		if (updates.containsKey("active")) {
			user.setActive((Boolean) updates.get("active"));
		}

		if (updates.containsKey("fullName")) {
			user.setFullName((String) updates.get("fullName"));
		}

		if (updates.containsKey("email")) {
			user.setEmail((String) updates.get("email"));
		}

		if (updates.containsKey("phoneNumber")) {
			user.setPhoneNumber((String) updates.get("phoneNumber"));
		}

		if (updates.containsKey("address")) {
			user.setAddress((String) updates.get("address"));
		}

		return userRepository.save(user);
	}

	// Delete a user
	@Transactional
	public void deleteUser(Long userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		} else {
			throw new RuntimeException("User not found with ID: " + userId);
		}
	}

	// Get total number of users
	public long getTotalUsers() {
		return userRepository.count();
	}

	// Get number of active users
	public long getActiveUsers() {
		return userRepository.countByActiveTrue();
	}

	// Admin dashboard statistics
	public AdminDashboardStats getAdminDashboardStats() {
		return new AdminDashboardStats(getTotalUsers(), getActiveUsers());
	}

	// Best performing category per user
	public Map<String, String> getBestCategoryPerUser() {
		List<Object[]> rawData = quizResultRepository.getAverageScoresPerUserPerCategory();
		Map<String, Map.Entry<String, Double>> userBestMap = new HashMap<>();

		for (Object[] row : rawData) {
			String username = (String) row[0];
			String category = (String) row[1];
			Double avgScore = ((Number) row[2]).doubleValue();

			if (!userBestMap.containsKey(username) || avgScore > userBestMap.get(username).getValue()) {
				userBestMap.put(username, Map.entry(category, avgScore));
			}
		}

		return userBestMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getKey()));
	}

	// Add this to AdminService
	public List<String> getAllCategories() {
		return securityControlRepository.findAll().stream().filter(control -> control.getCategoryGroup() != null)
				.map(SecurityControl::getName).collect(Collectors.toList());
	}

	// Fetch all valid SecurityControl categories for dropdown
	public List<SecurityControl> getAllSecurityControls() {
		return securityControlRepository.findAll().stream().filter(control -> control.getCategoryGroup() != null)
				.collect(Collectors.toList());
	}

	// Inner class for admin statistics
	public static class AdminDashboardStats {
		private final long totalUsers;
		private final long activeUsers;

		public AdminDashboardStats(long totalUsers, long activeUsers) {
			this.totalUsers = totalUsers;
			this.activeUsers = activeUsers;
		}

		public long getTotalUsers() {
			return totalUsers;
		}

		public long getActiveUsers() {
			return activeUsers;
		}
	}
}
