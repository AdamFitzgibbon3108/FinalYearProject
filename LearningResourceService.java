package com.example.service;

import com.example.model.LearningResource;
import com.example.model.SecurityControl;

import java.util.List;

public interface LearningResourceService {
    List<LearningResource> getResourcesByControl(SecurityControl control);
}

