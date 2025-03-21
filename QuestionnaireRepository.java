package com.example.repository;

import com.example.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    /**
     * Finds all questionnaires created by a specific admin.
     */
    List<Questionnaire> findByCreatedByAdminId(Long adminId);

    /**
     * Finds all available questionnaires.
     */
    List<Questionnaire> findAll();

    /**
     * Finds a specific questionnaire by its ID.
     */
    Optional<Questionnaire> findById(Long id);

    /**
     * Finds all questionnaires that contain a specific question.
     */
    @Query("SELECT q FROM Questionnaire q JOIN q.questions qu WHERE qu.id = :questionId")
    List<Questionnaire> findByQuestionId(@Param("questionId") Long questionId);

    /**
     * Deletes a questionnaire by its ID.
     */
    void deleteById(Long id);
}

