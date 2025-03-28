package com.example.controller;

import com.example.model.Response;
import com.example.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final ResponseService responseService;

    @Autowired
    public ResultController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @GetMapping
    public String showResultPage(Model model) {
        // Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch responses for this user
        List<Response> responses = responseService.getResponsesByUsername(username);

        // Add model attributes for Thymeleaf
        model.addAttribute("username", username);
        model.addAttribute("role", "General"); // Replace with actual role logic
        model.addAttribute("score", "N/A"); // Replace with actual scoring logic
        model.addAttribute("responses", responses);

        return "result"; // Must match `result.html`
    }
}

