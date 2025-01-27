package com.example.repository;

import com.example.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Question repository interface
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find questions by their control category (e.g., "General", "Admin")
    List<Question> findByControlCategory(String controlCategory);

    // You can add more methods to filter by other fields, such as difficulty, framework, etc.
    List<Question> findByControlCategoryAndDifficulty(String controlCategory, String difficulty);

    // Find all questions (if no filters are applied)
    List<Question> findAll();
}
