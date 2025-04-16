package com.example.controller;

import com.example.model.Question;
import com.example.model.QuizResult;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.UserRepository;
import com.example.service.ResponseService;
import dto.SubmittedAnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/responses")
public class ResponseController {

    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public ResponseController(ResponseService responseService,
                               UserRepository userRepository,
                               QuestionRepository questionRepository) {
        this.responseService = responseService;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @PostMapping("/submit")
    public String submitResponses(@RequestBody List<SubmittedAnswerDTO> responses,
                                   RedirectAttributes redirectAttributes) {
        // 1. Get authenticated username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Authenticated user: " + username);

        // 2. Fetch user from DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 3. Fetch all questions related to submitted answers
        List<Question> questions = responses.stream()
                .map(dto -> questionRepository.findById(dto.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found: " + dto.getQuestionId())))
                .collect(Collectors.toList());

        // 4. Save quiz result and related responses
        QuizResult result = responseService.saveQuizResultAndResponses(user, responses, questions);

        // 5. Set flash attributes for result page
        redirectAttributes.addFlashAttribute("quizResultId", result.getId());
        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("score", result.getTotalScore());
        redirectAttributes.addFlashAttribute("totalQuestions", result.getTotalQuestions());
        redirectAttributes.addFlashAttribute("passed", result.isPassed());
        redirectAttributes.addFlashAttribute("category", result.getCategory());
        redirectAttributes.addFlashAttribute("role", result.getRole());
        redirectAttributes.addFlashAttribute("recommendations", result.getRecommendations());

        return "redirect:/result";
    }
}
