package com.example.service;

import com.example.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> findByUsername(String username);
    void markSurveyAsCompleted(Long userId, String recommendedCategory);
}


