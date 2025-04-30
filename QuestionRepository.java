package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.Question;
import com.example.model.SecurityControl;

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
	 * Fetches questions by category and framework.
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
	 * Fetches questions by category.
	 */
	List<Question> findByCategory(String category);

	/**
	 * Fetches distinct roles from the questions table.
	 */
	@Query("SELECT DISTINCT q.role FROM Question q")
	List<String> findDistinctRoles();

	/**
	 * Fetches questions where questionText contains the given keyword
	 * (case-insensitive).
	 */
	List<Question> findByQuestionTextContainingIgnoreCase(String keyword);

	/**
	 * Fetches distinct categories from the questions table.
	 */
	@Query("SELECT DISTINCT q.category FROM Question q")
	List<String> findDistinctCategories();

	/**
	 * Fetches questions by control category ID and role.
	 */
	List<Question> findByControlCategory_IdAndRole(Long categoryId, String role);

	/**
	 * Checks if a question with the same text already exists (case-insensitive).
	 */
	boolean existsByTextIgnoreCase(String text);
}
