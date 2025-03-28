package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.SurveyQuestion;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

    // Fetch all questions with their predefined answer choices
    @EntityGraph(attributePaths = {"options"})
    List<SurveyQuestion> findAll();

    //  Fetch question by ID (if used in SurveyService)
    Optional<SurveyQuestion> findById(Long id);

    //  Fetch question by question text (if used in SurveyService)
    Optional<SurveyQuestion> findByQuestionText(String questionText);
}

