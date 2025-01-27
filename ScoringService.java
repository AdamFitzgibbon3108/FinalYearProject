package com.example.service;

import com.example.model.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    public int calculateScore(List<Response> responses) {
        int score = 0;
        for (Response response : responses) {
            if ("Yes".equalsIgnoreCase(response.getAnswer())) {
                score++;
            }
        }
        return score;
    }

	public void saveResponses(Map<String, String> responses) {
		
		
	}

	public int calculateScore(Map<String, String> responses) {
		
		return 0;
	}

	public void saveResponses(String username, Map<String, String> responses, int score) {
	
		
	}
}
