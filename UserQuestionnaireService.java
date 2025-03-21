package com.example.service;

import com.example.model.Question;
import com.example.model.User;
import com.example.model.UserQuestionnaire;
import com.example.repository.QuestionRepository;
import com.example.repository.UserQuestionnaireRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserQuestionnaireService {

    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Creates a new User Questionnaire for a specific user by username, with selected questions.
     */
    public UserQuestionnaire createUserQuestionnaire(String username, List<Long> selectedQuestionIds) {
        // ✅ Fix: Properly retrieve user from Optional<User>
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        // Retrieve questions from IDs
        List<Question> selectedQuestions = questionRepository.findAllById(selectedQuestionIds);

        UserQuestionnaire userQuestionnaire = new UserQuestionnaire();
        userQuestionnaire.setUser(user);
        userQuestionnaire.setSelectedQuestions(selectedQuestions); // Store selected questions

        return userQuestionnaireRepository.save(userQuestionnaire);
    }

    /**
     * Retrieves all questionnaires for a given user by username.
     */
    public List<UserQuestionnaire> getUserQuestionnaires(String username) {
        // ✅ Fix: Properly retrieve user from Optional<User>
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        return userQuestionnaireRepository.findByUserId(user.getId());
    }

    /**
     * Adds a question to a user's questionnaire.
     */
    @Transactional
    public void addQuestionToUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        if (questionnaire.getSelectedQuestions() == null) {
            questionnaire.setSelectedQuestions(new ArrayList<>());
        }

        if (!questionnaire.getSelectedQuestions().contains(question)) {
            questionnaire.getSelectedQuestions().add(question);
            userQuestionnaireRepository.save(questionnaire);
        } else {
            throw new IllegalArgumentException("Question already exists in the questionnaire.");
        }
    }

    /**
     * Removes a question from a user's questionnaire.
     */
    @Transactional
    public void removeQuestionFromUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        if (questionnaire.getSelectedQuestions() != null && questionnaire.getSelectedQuestions().contains(question)) {
            questionnaire.getSelectedQuestions().remove(question);
            userQuestionnaireRepository.save(questionnaire);
        }
    }

    /**
     * Retrieves a full summary of a user questionnaire (including user details and selected questions).
     */
    public Optional<UserQuestionnaire> getUserQuestionnaireSummary(Long questionnaireId) {
        return userQuestionnaireRepository.findById(questionnaireId);
    }
}

