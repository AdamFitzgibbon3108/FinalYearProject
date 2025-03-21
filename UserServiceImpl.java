package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAllUsers() {
        System.out.println("📋 Fetching all users with roles...");
        return userRepository.findAllWithRoles(); // Ensures roles are loaded properly
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

        //  Ensure at least a default role is assigned
        if (user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("USER")  // Fetch the default role
                    .orElseThrow(() -> new RuntimeException("Default role USER not found in DB"));
            user.getRoles().add(defaultRole);
        }

        // Save user
        User savedUser = userRepository.save(user);
        userRepository.flush();  // Ensures transaction commits

        System.out.println("✅ User successfully saved with ID: " + savedUser.getId());
        return savedUser;
    }


    @Override
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());

        //  Only update the password if it's provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        //   Ensure roles are managed entities before assigning
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
}
