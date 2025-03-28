package com.example.service;

import dto.UserPerformanceDTO;
import com.example.model.QuizResult;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.QuizResultRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAllUsers() {
        System.out.println("📋 Fetching all users with roles...");
        return userRepository.findAllWithRoles();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        String normalizedUsername = user.getUsername().toLowerCase();
        System.out.println("🔍 Checking if username exists: " + normalizedUsername);

        Optional<User> existingUser = userRepository.findByUsername(normalizedUsername);
        if (existingUser.isPresent()) {
            System.err.println("🚨 User registration blocked: Username already exists -> " + normalizedUsername);
            throw new RuntimeException("User already exists with username: " + normalizedUsername);
        }

        System.out.println("✅ No existing user found, proceeding to save new user: " + normalizedUsername);
        System.out.println("🔑 Raw password before saving: " + user.getPassword());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("🔐 Hashed password before saving: " + hashedPassword);

        user.setUsername(normalizedUsername);
        user.setPassword(hashedPassword);

        if (user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role USER not found in DB"));
            user.getRoles().add(defaultRole);
        }

        User savedUser = userRepository.save(user);
        userRepository.flush();

        System.out.println("✅ User successfully saved with ID: " + savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        Set<Role> managedRoles = new HashSet<>();
        for (Role role : updatedUser.getRoles()) {
            Role managedRole = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + role.getName()));
            managedRoles.add(managedRole);
        }
        existingUser.setRoles(managedRoles);

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("🗑 Deleting user with ID: " + id);
        userRepository.deleteById(id);
        System.out.println("✅ User deleted successfully.");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        System.out.println("🔍 Looking for user: " + username);
        return userRepository.findByUsername(username.toLowerCase());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        System.out.println("🔍 Fetching user by ID: " + id);
        return userRepository.findById(id);
    }

    @Override
    public void markSurveyAsCompleted(Long userId, String recommendedCategory) {
        System.out.println("📝 Marking survey as completed for user ID: " + userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSurveyCompleted(true);
            user.setRecommendedSecurityCategory(recommendedCategory);
            userRepository.save(user);
            System.out.println("✅ Survey marked as completed for user ID: " + userId);
        } else {
            System.err.println("❌ User not found for survey completion: " + userId);
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    @Override
    public List<UserPerformanceDTO> getAllUserPerformance() {
        List<User> users = userRepository.findAll();
        List<UserPerformanceDTO> performanceList = new ArrayList<>();

        for (User user : users) {
            List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

            int totalQuizzes = results.size();
            double avgScore = results.stream()
                    .mapToInt(QuizResult::getTotalScore)
                    .average()
                    .orElse(0.0);

            performanceList.add(new UserPerformanceDTO(
                    user.getId(),
                    user.getUsername(),
                    totalQuizzes,
                    avgScore
            ));
        }

        return performanceList;
    }

    // 🚫 Ban user
    @Override
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
        System.out.println("🔒 User banned: " + user.getUsername());
    }

    //  Unban user
    @Override
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
        System.out.println("🔓 User unbanned: " + user.getUsername());
    }

    //  Get all banned users
    @Override
    public List<User> getAllBannedUsers() {
        return userRepository.findByBannedTrue();
    }

    //  Check if user is banned
    @Override
    public boolean isUserBanned(Long userId) {
        return userRepository.findById(userId)
                .map(User::isBanned)
                .orElse(false);
    }

    // ✅ Add missing methods to match interface
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

	@Override
	public List<User> getAllActiveUsers() {
		
		return userRepository.findByBannedFalse();
	}
}