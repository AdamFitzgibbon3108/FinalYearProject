package com.example.service;

import com.example.model.Question;
import com.example.model.Response;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Submits a response for a user and automatically assigns a score.
     */
    public Response submitResponse(Long questionId, Long userId, String answer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Check if the answer is correct
        boolean isCorrect = answer.equalsIgnoreCase(question.getCorrectAnswer());
        int score = isCorrect ? question.getScore() : 0;  // Assign score based on correctness

        Response response = new Response();
        response.setQuestion(question);
        response.setUser(user);
        response.setAnswer(answer);
        response.setScore(score);
        response.setTimestamp(LocalDateTime.now()); // ✅ Keep timestamp

        return responseRepository.save(response);
    }

    /**
     * Saves a response in the database.
     */
    public Response saveResponse(Response response) {
        return responseRepository.save(response);
    }

    /**
     * Retrieves all responses by a user.
     */
    public List<Response> getResponsesByUser(Long userId) {
        return responseRepository.findByUserId(userId);
    }

    /**
     * Retrieves all responses for a given question.
     */
    public List<Response> getResponsesByQuestion(Long questionId) {
        return responseRepository.findByQuestionId(questionId);
    }
}
