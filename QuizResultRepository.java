package com.example.repository;

import com.example.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUserUsername(String username);

    List<QuizResult> findByUserId(Long userId);

    @Query("SELECT sc.name, AVG(q.totalScore) " +
           "FROM QuizResult q JOIN SecurityControl sc ON q.category = CAST(sc.id AS string) " +
           "WHERE q.user.username = :username " +
           "GROUP BY sc.name")
    List<Object[]> getAverageScoreByCategoryName(@Param("username") String username);

    @Query("SELECT sc.categoryGroup, COUNT(q.id) " +
           "FROM QuizResult q JOIN SecurityControl sc ON q.category = CAST(sc.id AS string) " +
           "WHERE q.user.username = :username " +
           "GROUP BY sc.categoryGroup")
    List<Object[]> getQuizCountByCategoryGroup(@Param("username") String username);

    @Query("SELECT q FROM QuizResult q LEFT JOIN FETCH q.responses WHERE q.id = :quizId")
    Optional<QuizResult> findByIdWithResponses(@Param("quizId") Long quizId);

    //  New: Count by role for chart updates
    @Query("SELECT COUNT(q) FROM QuizResult q WHERE q.role = :role")
    Long countByRole(@Param("role") String role);
}
