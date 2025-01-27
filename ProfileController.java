package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "editProfile"; // Corresponds to editProfile.html
    }

    @PostMapping("/edit")
    public String updateProfile(User updatedUser, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        // Update user details
        currentUser.setPassword(updatedUser.getPassword()); // Assuming password can be updated
        userService.updateUser(currentUser.getId(), currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("successMessage", "Profile updated successfully!");
        return "profile";
    }
}
