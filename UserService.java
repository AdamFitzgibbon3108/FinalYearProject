package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.User;

import dto.UserPerformanceDTO;

public interface UserService {
	List<User> getAllUsers();

	Optional<User> getUserById(Long id);

	User createUser(User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);

	List<UserPerformanceDTO> getAllUserPerformance();

	Optional<User> findByUsername(String username);

	void markSurveyAsCompleted(Long userId, String recommendedCategory);

	Optional<User> findById(Long id);

	User save(User user);

	List<User> getAllActiveUsers();
}
