package com.example.service;

import com.example.model.QuizResult;

import java.util.List;

public interface QuizResultService {
    List<QuizResult> findByUserId(Long userId);
}

