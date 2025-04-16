package com.example.service;

import com.example.model.QuizResult;

import java.util.List;
import java.util.Optional;

public interface QuizResultService {
    List<QuizResult> findByUserId(Long userId);
    QuizResult saveResult(QuizResult quizResult);
    Optional<QuizResult> findByIdWithResponses(Long quizId);

    // New method
    Long countByRole(String role);
}

