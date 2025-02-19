package com.example.repository;

import com.example.model.SecurityControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityControlRepository extends JpaRepository<SecurityControl, Long> {
    SecurityControl findByName(String name);
}

