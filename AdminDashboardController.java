package com.example.controller;

import dto.UserPerformanceDTO;
import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.QuizResult;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.AdminService.AdminDashboardStats;
import com.example.service.QuizResultService;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

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
        model.addAttribute("pendingUsers", stats.getPendingApprovals());

        return "admin-dashboard";
    }

    @GetMapping("/questions")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", adminService.getAllQuestions());
        return "manage-questions";
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
    public ResponseEntity<Questionnaire> createQuestionnaire(
            @RequestBody Map<String, Object> requestData,
            Authentication authentication) {
        String title = (String) requestData.get("title");
        String description = (String) requestData.get("description");
        String adminUsername = authentication.getName();

        return ResponseEntity.ok(adminService.createQuestionnaire(title, description, adminUsername));
    }

    @PostMapping("/api/questionnaires/{questionnaireId}/assign")
    public ResponseEntity<Questionnaire> assignQuestionsToQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {
        return ResponseEntity.ok(adminService.assignQuestionsToQuestionnaire(questionnaireId, questionIds));
    }

    @PostMapping("/api/questionnaires/{questionnaireId}/remove")
    public ResponseEntity<Questionnaire> removeQuestionsFromQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {
        return ResponseEntity.ok(adminService.removeQuestionsFromQuestionnaire(questionnaireId, questionIds));
    }

    @DeleteMapping("/api/questionnaires/{questionnaireId}")
    public ResponseEntity<String> deleteQuestionnaire(@PathVariable Long questionnaireId) {
        adminService.deleteQuestionnaire(questionnaireId);
        return ResponseEntity.ok("Questionnaire deleted successfully.");
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/api/users/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updates) {
        User updatedUser = adminService.updateUser(userId, updates);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/api/users/{userId}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long userId,
            @RequestParam String role,
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
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("results", results);
        return "admin-user-quiz-history";
    }

    @PostMapping("/toggle-ban")
    public String toggleUserBan(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBanned(!user.isBanned());
            userService.updateUser(user.getId(), user);
            redirectAttributes.addFlashAttribute("message", "User ban status updated.");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/admin/ban";
    }

    @GetMapping("/ban")
    public String viewBannedUsers(Model model) {
        List<User> bannedUsers = userService.getAllBannedUsers();
        List<User> activeUsers = userService.getAllActiveUsers();
        model.addAttribute("bannedUsers", bannedUsers);
        model.addAttribute("activeUsers", activeUsers);
        return "admin-banned-users";
    }

    //  New Endpoint for Chart Data
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

