package com.example.repository;

import com.example.model.LearningResource;
import com.example.model.SecurityControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    List<LearningResource> findByControl(SecurityControl control);
    

}

