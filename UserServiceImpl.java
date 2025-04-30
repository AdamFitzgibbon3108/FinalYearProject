package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.QuizResult;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.QuizResultRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;

import dto.UserPerformanceDTO;

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
			throw new RuntimeException("User already exists with username: " + normalizedUsername);
		}

		user.setUsername(normalizedUsername);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (user.getRoles().isEmpty()) {
			Role defaultRole = roleRepository.findByName("USER")
					.orElseThrow(() -> new RuntimeException("Default role USER not found in DB"));
			user.getRoles().add(defaultRole);
		}

		User savedUser = userRepository.save(user);
		userRepository.flush();
		return savedUser;
	}

	@Override
	@Transactional
	public User updateUser(Long id, User updatedUser) {
		User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
			existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}

		existingUser.setFullName(updatedUser.getFullName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
		existingUser.setAddress(updatedUser.getAddress());

		return userRepository.save(existingUser);
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username.toLowerCase());
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public void markSurveyAsCompleted(Long userId, String recommendedCategory) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		user.setSurveyCompleted(true);
		user.setRecommendedSecurityCategory(recommendedCategory);
		userRepository.save(user);
	}

	@Override
	public List<UserPerformanceDTO> getAllUserPerformance() {
		List<User> users = userRepository.findAll();
		List<UserPerformanceDTO> performanceList = new ArrayList<>();

		for (User user : users) {
			List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

			int totalQuizzes = results.size();
			double avgScore = results.stream().mapToInt(QuizResult::getTotalScore).average().orElse(0.0);

			performanceList.add(new UserPerformanceDTO(user.getId(), user.getUsername(), totalQuizzes, avgScore));
		}
		return performanceList;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllActiveUsers() {
		return userRepository.findAllWithRoles(); // All users = Active users (no banned concept anymore)
	}
}
