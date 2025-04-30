package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.User;
import com.example.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private UserService userService;

	@GetMapping
	public String viewProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userService.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found with username: " + username));

		model.addAttribute("user", user);
		return "profile"; // profile.html
	}

	@GetMapping("/edit")
	public String editProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userService.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found with username: " + username));

		model.addAttribute("user", user);
		return "editProfile"; // editProfile.html
	}

	@PostMapping("/edit")
	public String updateProfile(User updatedUser) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User currentUser = userService.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found with username: " + username));

		// Update editable fields
		currentUser.setFullName(updatedUser.getFullName());
		currentUser.setEmail(updatedUser.getEmail());
		currentUser.setPhoneNumber(updatedUser.getPhoneNumber());
		currentUser.setAddress(updatedUser.getAddress());

		userService.save(currentUser);

		return "profile-success"; // Redirect to success page after saving
	}
}
