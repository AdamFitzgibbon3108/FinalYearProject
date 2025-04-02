package com.example.controller;

import com.example.model.QuizResult;
import com.example.repository.QuizResultRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserQuizHistoryController {

    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserQuizHistoryController(QuizResultRepository quizResultRepository, UserRepository userRepository) {
        this.quizResultRepository = quizResultRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/quiz-history")
    public String getUserQuizHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Retrieve all quiz results for the current user
        List<QuizResult> results = quizResultRepository.findByUserUsername(username);

        model.addAttribute("results", results);
        model.addAttribute("username", username);
        return "user-quiz-history"; // Will resolve to templates/user-quiz-history.html
    }
}

