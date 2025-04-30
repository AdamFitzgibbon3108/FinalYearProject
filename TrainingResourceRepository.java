package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.TrainingResource;

@Repository
public interface TrainingResourceRepository extends JpaRepository<TrainingResource, Integer> {

	// Find resources by category name (case-insensitive)
	List<TrainingResource> findByCategoryNameIgnoreCase(String categoryName);

	// Find all resources for all category's
	List<TrainingResource> findAll();

}
