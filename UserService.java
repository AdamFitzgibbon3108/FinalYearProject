package com.example.service;

import com.example.model.User;
import dto.UserPerformanceDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<UserPerformanceDTO> getAllUserPerformance();
    Optional<User> findByUsername(String username);
    void markSurveyAsCompleted(Long userId, String recommendedCategory);

    //  Ban/Unban-related methods
    void banUser(Long userId);
    void unbanUser(Long userId);
    List<User> getAllBannedUsers();
    boolean isUserBanned(Long userId);

    //  Add these two for completeness (used in controller)
    Optional<User> findById(Long id);
    User save(User user);
	List<User> getAllActiveUsers();
}

