package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.SurveyResponse;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

	// Fetch all responses for a given user
	List<SurveyResponse> findByUser_Username(String username);

	// method to check if the user already submitted any survey response
	boolean existsByUserUsername(String username);
}
