package com.example.service;

import com.example.model.QuizResult;
import com.example.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizResultServiceImpl implements QuizResultService {

    private final QuizResultRepository quizResultRepository;

    @Autowired
    public QuizResultServiceImpl(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    @Override
    public List<QuizResult> findByUserId(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }

    @Override
    public QuizResult saveResult(QuizResult quizResult) {
        return quizResultRepository.save(quizResult);
    }

    @Override
    public Optional<QuizResult> findByIdWithResponses(Long quizId) {
        return quizResultRepository.findByIdWithResponses(quizId);
    }

    @Override
    public Long countByRole(String role) {
        return quizResultRepository.countByRole(role);
    }

    public int calculateRoundedPercentage(int totalScore, int totalPossibleScore) {
        if (totalPossibleScore == 0) return 0;
        return (int) Math.round(((double) totalScore / totalPossibleScore) * 100);
    }
}

