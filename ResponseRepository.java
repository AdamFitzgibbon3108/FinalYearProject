package com.example.repository;

import com.example.model.Response;
import com.example.model.User;
import com.example.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    /**
     * Fetches all responses for a given user.
     */
    List<Response> findByUser(User user);

    /**
     * Fetches all responses for a given question.
     */
    List<Response> findByQuestion(Question question);

    /**
     * Fetches responses for a specific user and question.
     */
    Response findByUserAndQuestion(User user, Question question);

	List<Response> findByUserId(Long userId);

	List<Response> findByQuestionId(Long questionId);
}



