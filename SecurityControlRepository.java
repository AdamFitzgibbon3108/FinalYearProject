package com.example.repository;

import com.example.model.SecurityControl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityControlRepository extends JpaRepository<SecurityControl, Long> {
    SecurityControl findByName(String name);
    
    Optional<SecurityControl> findByNameIgnoreCase(String name);

    
    Optional<SecurityControl> findById(Long id);

}

