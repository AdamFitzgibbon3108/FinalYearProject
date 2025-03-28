package com.example.service;

import com.example.model.QuizResult;
import com.example.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
