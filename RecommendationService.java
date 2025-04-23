package com.example.service;

import com.example.model.Recommendation;
import com.example.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public String getRandomRecommendation(String category) {
    	System.out.println(">> [DEBUG] Fetching DB recommendation for category: " + category);
        List<Recommendation> recs = recommendationRepository.findByCategoryIgnoreCase(category);
        if (recs.isEmpty()) {
            return "Keep practicing! More targeted advice will be available soon.";
        }
        return recs.get(new Random().nextInt(recs.size())).getMessage();
    }

    public List<Recommendation> getAllByCategory(String category) {
        return recommendationRepository.findByCategoryIgnoreCase(category);
    }
}
