package com.example.controller;

import com.example.model.Response;
import com.example.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/responses")
public class ResponseController {

    private final ResponseService responseService;

    @Autowired
    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * Handles user responses and redirects to the result page.
     */
    @PostMapping("/submit")
    public String submitResponses(@RequestBody List<Response> responses, RedirectAttributes redirectAttributes) {
        // Save all responses
        responses.forEach(responseService::saveResponse);

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Pass data to result page
        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("role", "General"); // Replace with actual role
        redirectAttributes.addFlashAttribute("score", "N/A"); // Placeholder until scoring is added

        return "redirect:/result";
    }
}



