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

    // Case-insensitive + trimmed match for category name
    public Optional<SecurityControl> findByNameIgnoreCase(String name) {
        return securityControlRepository.findByNameIgnoreCase(name.trim());
    }

    public void printAllControlNames() {
        List<SecurityControl> controls = getAllSecurityControls();
        System.out.println("[DEBUG] All available SecurityControl names:");
        for (SecurityControl sc : controls) {
            System.out.println(" - '" + sc.getName() + "'");
        }
    }


    // Delete a security control by ID
    public void deleteSecurityControl(Long id) {
        securityControlRepository.deleteById(id);
    }
}

