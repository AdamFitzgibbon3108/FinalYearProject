package com.example.repository;

import com.example.model.Question;
import com.example.model.SecurityControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Fetches questions by role and difficulty.
     */
    List<Question> findByRoleAndDifficulty(String role, String difficulty);

    /**
     * Fetches all questions belonging to a given security control category.
     */
    List<Question> findByControlCategory(SecurityControl controlCategory);

    /**
     * Fetches all questions by category and framework.
     */
    List<Question> findByCategoryAndFramework(String category, String framework);

    /**
     * Fetches questions by role and category.
     */
    List<Question> findByRoleAndCategory(String role, String category);
    
   
    
    /**
     * Fetches questions by role.
     */
    List<Question> findByRole(String role);
    
    /**
     * Fetches all questions by category.
     */
    List<Question> findByCategory(String category);

    /**
     * Fetches distinct roles from the questions table.
     */
    @Query("SELECT DISTINCT q.role FROM Question q")
    List<String> findDistinctRoles();

    /**
     * Fetches distinct categories from the questions table.
     */
    @Query("SELECT DISTINCT q.category FROM Question q")
    List<String> findDistinctCategories();
}






