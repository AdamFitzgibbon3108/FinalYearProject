package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    /**
     * Display the dashboard page.
     * Ensures users see a personalized dashboard with their username.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);
        model.addAttribute("welcomeMessage", "Welcome to your Dashboard!");

        return "dashboard"; // Redirects to dashboard.html
    }

    /**
     * Redirects the user to the questionnaire selection page.
     * This ensures they can choose role, category, or create a custom questionnaire.
     */
    @GetMapping("/start-questionnaire")
    public String startQuestionnaire() {
        return "redirect:/questionnaire/selection";
    }
}
