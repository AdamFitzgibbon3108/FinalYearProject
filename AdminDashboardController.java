package com.example.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.QuizResult;
import com.example.model.SecurityControl;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.AdminService.AdminDashboardStats;
import com.example.service.QuizResultService;
import com.example.service.UserService;

import dto.UserPerformanceDTO;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserService userService;

	@Autowired
	private QuizResultService quizResultService;

	@GetMapping("/dashboard")
	public String adminDashboard(Model model, Principal principal) {
		model.addAttribute("adminName", principal.getName());

		AdminDashboardStats stats = adminService.getAdminDashboardStats();
		model.addAttribute("totalUsers", stats.getTotalUsers());
		model.addAttribute("activeUsers", stats.getActiveUsers());
		model.addAttribute("bestCategoryPerUser", adminService.getBestCategoryPerUser());

		return "admin-dashboard";
	}

	@GetMapping("/questions")
	public String manageQuestions(Model model) {
		model.addAttribute("questions", adminService.getAllQuestions());
		model.addAttribute("categories", adminService.getAllCategories()); // for legacy dropdown fallback
		return "manage-questions";
	}

	@GetMapping("/api/security-controls")
	@ResponseBody
	public List<SecurityControl> getAllSecurityControls() {
		return adminService.getAllSecurityControls();
	}

	@GetMapping("/questionnaires")
	public String manageQuestionnaires(Model model) {
		model.addAttribute("questionnaires", adminService.getAllQuestionnaires());
		return "manage-questionnaires";
	}

	@GetMapping("/api/questions")
	public ResponseEntity<List<Question>> getAllQuestions() {
		return ResponseEntity.ok(adminService.getAllQuestions());
	}

	@GetMapping("/api/questionnaires")
	public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
		return ResponseEntity.ok(adminService.getAllQuestionnaires());
	}

	@GetMapping("/api/questionnaires/{questionnaireId}")
	public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
		return ResponseEntity.ok(adminService.getQuestionnaireById(questionnaireId));
	}

	@PostMapping("/api/questionnaires")
	public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Map<String, Object> requestData,
			Authentication authentication) {
		String title = (String) requestData.get("title");
		String description = (String) requestData.get("description");
		String adminUsername = authentication.getName();

		return ResponseEntity.ok(adminService.createQuestionnaire(title, description, adminUsername));
	}

	@PostMapping("/api/questionnaires/{questionnaireId}/assign")
	public ResponseEntity<Questionnaire> assignQuestionsToQuestionnaire(@PathVariable Long questionnaireId,
			@RequestBody List<Long> questionIds) {
		return ResponseEntity.ok(adminService.assignQuestionsToQuestionnaire(questionnaireId, questionIds));
	}

	@PostMapping("/api/questionnaires/{questionnaireId}/remove")
	public ResponseEntity<Questionnaire> removeQuestionsFromQuestionnaire(@PathVariable Long questionnaireId,
			@RequestBody List<Long> questionIds) {
		return ResponseEntity.ok(adminService.removeQuestionsFromQuestionnaire(questionnaireId, questionIds));
	}

	@DeleteMapping("/api/questionnaires/{questionnaireId}")
	public ResponseEntity<String> deleteQuestionnaire(@PathVariable Long questionnaireId) {
		adminService.deleteQuestionnaire(questionnaireId);
		return ResponseEntity.ok("Questionnaire deleted successfully.");
	}

	@GetMapping("/api/users")
	@ResponseBody
	public List<String> getAllUsernames() {
		return adminService.getAllUsers().stream().map(User::getUsername).collect(Collectors.toList());
	}

	@GetMapping("/api/users/active")
	@ResponseBody
	public List<String> getActiveUsernames() {
		return adminService.getAllUsers().stream().filter(User::isActive).map(User::getUsername)
				.collect(Collectors.toList());
	}

	@PutMapping("/api/users/{userId}")
	public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {
		User updatedUser = adminService.updateUser(userId, updates);
		return ResponseEntity.ok(updatedUser);
	}

	@PostMapping("/api/users/{userId}/roles")
	public ResponseEntity<String> updateUserRoles(@PathVariable Long userId, @RequestParam String role,
			@RequestParam boolean assign) {
		if (assign) {
			adminService.assignRoleToUser(userId, role);
			return ResponseEntity.ok("Role assigned successfully.");
		} else {
			adminService.removeRoleFromUser(userId, role);
			return ResponseEntity.ok("Role removed successfully.");
		}
	}

	@DeleteMapping("/api/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		adminService.deleteUser(userId);
		return ResponseEntity.ok("User deleted successfully.");
	}

	@GetMapping("/performance")
	public String viewAllUserPerformance(Model model) {
		List<UserPerformanceDTO> performanceList = userService.getAllUserPerformance();
		model.addAttribute("performanceList", performanceList);
		return "admin-performance";
	}

	@GetMapping("/performance/{userId}")
	public String viewUserQuizHistory(@PathVariable Long userId, Model model) {
		List<QuizResult> results = quizResultService.findByUserId(userId);
		User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("user", user);
		model.addAttribute("results", results);
		return "admin-user-quiz-history";
	}

	@GetMapping("/api/questionnaires-taken-per-role")
	@ResponseBody
	public Map<String, Long> getQuestionnairesTakenPerRole() {
		Map<String, Long> result = new HashMap<>();
		result.put("Manager", quizResultService.countByRole("Manager"));
		result.put("Analyst", quizResultService.countByRole("Analyst"));
		result.put("Developer", quizResultService.countByRole("Developer"));
		result.put("General", quizResultService.countByRole("General"));
		return result;
	}
}
