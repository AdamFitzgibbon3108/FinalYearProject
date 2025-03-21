package com.example.repository;

import com.example.model.SurveyQuestionOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionOptionsRepository extends JpaRepository<SurveyQuestionOptions, Long> {

    //  Fetch all predefined answer choices for a given question
    List<SurveyQuestionOptions> findBySurveyQuestionId(Long surveyQuestionId);

    //  Find a survey option by its value (used in SurveyService)
    Optional<SurveyQuestionOptions> findByOptionValue(String optionValue);
}
