package com.example.service;

import com.example.model.Response;
import com.example.model.User;
import com.example.model.Question;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;  // Add this to your service class

    
    @Autowired
    private QuestionRepository questionRepository;

    // Method to submit a response
    public void submitResponse(Long questionId, Long userId, String answer, int score) {
        // Retrieve the Question entity by its ID
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        // Retrieve the User entity by its ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Create a new Response entity
        Response response = new Response();
        response.setQuestion(question);  // Setting the whole Question entity
        response.setUser(user);          // Set the User entity directly, not just userId
        response.setAnswer(answer);
        response.setScore(score);
        response.setTimestamp(LocalDateTime.now());

        // Save the Response entity
        responseRepository.save(response);  // Use the save method to store the response
    }


    // Optionally, if you want to expose saveResponse for other uses:
    public Response saveResponse(Response response) {
        return responseRepository.save(response); // This will save the response directly
    }
}
