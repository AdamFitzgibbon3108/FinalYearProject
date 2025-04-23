package com.example.controller;

import com.example.model.Question;
import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.model.SecurityControl;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.QuizResultRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import com.example.repository.SecurityControlRepository;
import com.example.service.QuestionService;
import com.example.service.RecommendationService;
import com.example.service.ScoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions")
public class QuestionController {

	@Autowired
	private RecommendationService recommendationService;

	
    @Autowired
    private ScoringService scoringService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private SecurityControlRepository securityControlRepository;

    // Display all questions + categories
    @GetMapping("/manage")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        model.addAttribute("categories", questionService.getAllCategories());
        return "manage-questions";
    }

    // Add a new question
    @PostMapping("/add")
    public String addQuestion(@RequestParam String question_text,
                              @RequestParam String options,
                              @RequestParam String category) {
        Question question = new Question();
        question.setQuestionText(question_text);
        question.setOptions(options);
        question.setCategory(category);
        questionRepository.save(question);
        return "redirect:/questions/manage";
    }

    // Update a question inline
    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 @RequestParam String question_text,
                                 @RequestParam String options) {
        Optional<Question> optional = questionRepository.findById(id);
        if (optional.isPresent()) {
            Question question = optional.get();
            question.setQuestionText(question_text);
            question.setOptions(options);
            questionRepository.save(question);
        }
        return "redirect:/questions/manage";
    }

    // Delete a question inline
    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return "redirect:/questions/manage";
    }

    // Search by keyword
    @GetMapping("/search")
    public String searchQuestions(@RequestParam String keyword, Model model) {
        List<Question> results = questionService.searchByKeyword(keyword);
        model.addAttribute("questions", results);
        model.addAttribute("categories", questionService.getAllCategories());
        return "manage-questions";
    }

    // Filter by category
    @GetMapping("/filter")
    public String filterByCategory(@RequestParam String category, Model model) {
        List<Question> results = questionService.getQuestionsByCategory(category);
        model.addAttribute("questions", results);
        model.addAttribute("categories", questionService.getAllCategories());
        return "manage-questions";
    }

    // Get grouped categories
    @GetMapping("/grouped-categories")
    @ResponseBody
    public Map<String, List<String>> getGroupedCategories() {
        List<SecurityControl> allControls = securityControlRepository.findAll();

        return allControls.stream()
                .filter(control -> control.getCategoryGroup() != null)
                .collect(Collectors.groupingBy(
                        SecurityControl::getCategoryGroup,
                        TreeMap::new,
                        Collectors.mapping(SecurityControl::getName, Collectors.toList())
                ));
    }

    @GetMapping("/roles")
    @ResponseBody
    public List<String> getAllRoles() {
        return questionService.getAllRoles();
    }

    @GetMapping("/questionnaire/custom")
    public String customQuestionnaire(Model model) {
        List<Question> allQuestions = questionService.getAllQuestions();
        model.addAttribute("questions", allQuestions);
        return "custom-questionnaire";
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<String> getAllCategories() {
        return questionService.getAllCategories();
    }

    @GetMapping("/category/{category}")
    @ResponseBody
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @GetMapping("/start-role-only")
    public String startRoleBasedQuestionnaire(@RequestParam("selectedRole") String selectedRole, Model model) {
        List<Question> questions = questionService.getQuestionsByRole(selectedRole);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", "None");
        return "questionnaire";
    }

    @GetMapping("/start")
    public String startQuestionnaire(@RequestParam("selectedRole") String selectedRole,
                                     @RequestParam(value = "selectedCategory", required = false) String selectedCategory,
                                     Model model) {
        List<Question> questions = questionService.getQuestionsByRoleAndCategory(selectedRole, selectedCategory);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", selectedCategory);
        return "questionnaire";
    }

    @Transactional
    @PostMapping("/submit")
    public String submitQuestionnaire(@RequestParam Map<String, String> requestBody, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found.");
            return "result";
        }
        User user = userOptional.get();

        String selectedRole = requestBody.get("selectedRole");
        String selectedCategory = requestBody.get("selectedCategory");

        if (selectedRole == null || selectedCategory == null) {
            model.addAttribute("error", "Missing role or category.");
            return "result";
        }

        List<Response> responsesToSave = new ArrayList<>();
        int totalScore = 0;
        int totalQuestions = 0;

        for (Map.Entry<String, String> entry : requestBody.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                    String answer = entry.getValue();

                    Optional<Question> questionOptional = questionRepository.findById(questionId);
                    if (questionOptional.isPresent()) {
                        Question question = questionOptional.get();
                        String correctAnswer = question.getCorrectAnswer();

                        int score = correctAnswer.trim().equalsIgnoreCase(answer.trim()) ? 1 : 0;
                        totalScore += score;
                        totalQuestions++;

                        Response response = new Response();
                        response.setUser(user);
                        response.setQuestion(question);
                        response.setScore(score);
                        response.setTimestamp(LocalDateTime.now());
                        response.setRole(selectedRole);
                        response.setCategory(selectedCategory);
                        response.setSelectedAnswer(answer);
                        response.setCorrectAnswer(correctAnswer);

                        responsesToSave.add(response);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        double percentage = ((double) totalScore / totalQuestions) * 100.0;
        boolean passed = percentage >= 80.0;

        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setTotalScore(totalScore);
        result.setTotalQuestions(totalQuestions);
        result.setCategory(selectedCategory);
        result.setRole(selectedRole);
        result.setCompletedAt(LocalDateTime.now());
        result.setScorePercentage(percentage);
        result.setPassed(passed);
        quizResultRepository.saveAndFlush(result); // ensure ID is available

        for (Response response : responsesToSave) {
            response.setQuizResult(result); // link each response to the result
            responseRepository.save(response);
        }

        String recommendation = recommendationService.getRandomRecommendation(selectedCategory);
      
        String trainingProgram = scoringService.suggestTrainingProgram(percentage);

        model.addAttribute("username", username);
        model.addAttribute("role", selectedRole);
        model.addAttribute("category", selectedCategory);
        model.addAttribute("score", totalScore);
        model.addAttribute("percentageScore", percentage);
        model.addAttribute("passed", passed);
        model.addAttribute("trainingProgram", trainingProgram);
        model.addAttribute("responses", responsesToSave);
        model.addAttribute("recommendations", recommendation);

        return "redirect:/result";
    }
}
