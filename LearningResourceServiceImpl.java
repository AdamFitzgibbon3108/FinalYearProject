package com.example.service;

import com.example.model.LearningResource;
import com.example.model.SecurityControl;
import com.example.repository.LearningResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningResourceServiceImpl implements LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;

    @Autowired
    public LearningResourceServiceImpl(LearningResourceRepository learningResourceRepository) {
        this.learningResourceRepository = learningResourceRepository;
    }

    @Override
    public List<LearningResource> getResourcesByControl(SecurityControl control) {
        System.out.println("[DEBUG] Fetching Learning Resources for Control:");
        System.out.println(" - ID: " + control.getId());
        System.out.println(" - Name: " + control.getName());

        List<LearningResource> list = learningResourceRepository.findByControl(control);

        System.out.println("[DEBUG] Retrieved " + list.size() + " resources.");
        for (LearningResource resource : list) {
            System.out.println("   → " + resource.getTitle() + " | " + resource.getUrl());
        }

        return list;
    }

    
    
}

