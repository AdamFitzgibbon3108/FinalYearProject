package com.example.repository;

import com.example.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
   
    List<QuizResult> findByUserUsername(String username);

    
    List<QuizResult> findByUserId(Long userId);
}
