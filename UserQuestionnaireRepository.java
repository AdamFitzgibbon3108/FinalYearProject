package com.example.repository;

import com.example.model.UserQuestionnaire;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionnaireRepository extends JpaRepository<UserQuestionnaire, Long> {
    List<UserQuestionnaire> findByUser(User user);

	List<UserQuestionnaire> findByUserId(Long userId);
}

