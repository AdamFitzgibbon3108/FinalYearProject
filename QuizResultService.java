package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.QuizResult;

public interface QuizResultService {
	List<QuizResult> findByUserId(Long userId);

	QuizResult saveResult(QuizResult quizResult);

	Optional<QuizResult> findByIdWithResponses(Long quizId);

	Long countByRole(String role);
}
