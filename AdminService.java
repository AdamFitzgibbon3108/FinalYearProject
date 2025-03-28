package com.example.service;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.QuestionnaireRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //  Get all questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    //  Get all questionnaires
    public List<Questionnaire> getAllQuestionnaires() {
        return questionnaireRepository.findAll();
    }

    //  Get a specific questionnaire by ID
    public Questionnaire getQuestionnaireById(Long questionnaireId) {
        return questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found with ID: " + questionnaireId));
    }

    //  Create a new questionnaire and assign the creator
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

    //  Assign questions to a questionnaire
    @Transactional
    public Questionnaire assignQuestionsToQuestionnaire(Long questionnaireId, List<Long> questionIds) {
        Questionnaire questionnaire = getQuestionnaireById(questionnaireId);
        List<Question> selectedQuestions = questionRepository.findAllById(questionIds);
        questionnaire.getQuestions().addAll(selectedQuestions);
        return questionnaireRepository.save(questionnaire);
    }

    //  Remove questions from a questionnaire
    @Transactional
    public Questionnaire removeQuestionsFromQuestionnaire(Long questionnaireId, List<Long> questionIds) {
        Questionnaire questionnaire = getQuestionnaireById(questionnaireId);
        questionnaire.getQuestions().removeIf(q -> questionIds.contains(q.getId()));
        return questionnaireRepository.save(questionnaire);
    }

    //  Delete a questionnaire
    @Transactional
    public void deleteQuestionnaire(Long questionnaireId) {
        if (questionnaireRepository.existsById(questionnaireId)) {
            questionnaireRepository.deleteById(questionnaireId);
        } else {
            throw new RuntimeException("Questionnaire not found with ID: " + questionnaireId);
        }
    }

    //  Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //  Assign a role to a user
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (!user.getRoles().contains(role)) {
            user.addRole(role);
            userRepository.save(user);
        }
    }

    //  Remove a role from a user
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        userRepository.save(user);
    }

    //  Update user details (Removed setEmail method)
    @Transactional
    public User updateUser(Long userId, Map<String, Object> updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("username")) {
            String newUsername = (String) updates.get("username");
            if (newUsername != null && !newUsername.isEmpty()) {
                user.setUsername(newUsername);
            }
        }

        if (updates.containsKey("active")) {
            user.setActive((Boolean) updates.get("active"));
        }

        return userRepository.save(user);
    }


    //  Delete a user
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    //  Get total number of users
    public long getTotalUsers() {
        return userRepository.count();
    }

    //  Get number of active users
    public long getActiveUsers() {
        return userRepository.countActiveUsers(); // Ensure this method exists in UserRepository
    }

    //  Get number of pending user approvals
    public long getPendingApprovals() {
        return userRepository.countPendingUsers(); // Ensure this method exists in UserRepository
    }

    // Get statistics for admin dashboard
    public AdminDashboardStats getAdminDashboardStats() {
        return new AdminDashboardStats(
                getTotalUsers(),
                getActiveUsers(),
                getPendingApprovals()
        );
    }

    //  Inner class for admin dashboard statistics
    public static class AdminDashboardStats {
        private final long totalUsers;
        private final long activeUsers;
        private final long pendingApprovals;

        public AdminDashboardStats(long totalUsers, long activeUsers, long pendingApprovals) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.pendingApprovals = pendingApprovals;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getPendingApprovals() {
            return pendingApprovals;
        }
    }
}



