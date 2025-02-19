package com.example.service;

import com.example.model.SecurityControl;
import com.example.repository.SecurityControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityControlService {

    @Autowired
    private SecurityControlRepository securityControlRepository;

    // Create or update a security control
    public SecurityControl saveSecurityControl(SecurityControl securityControl) {
        return securityControlRepository.save(securityControl);
    }

    // Get a security control by ID
    public Optional<SecurityControl> getSecurityControlById(Long id) {
        return securityControlRepository.findById(id);
    }

    // Get all security controls
    public List<SecurityControl> getAllSecurityControls() {
        return securityControlRepository.findAll();
    }

    // Get a security control by name
    public SecurityControl getSecurityControlByName(String name) {
        return securityControlRepository.findByName(name);
    }

    // Delete a security control by ID
    public void deleteSecurityControl(Long id) {
        securityControlRepository.deleteById(id);
    }
}

