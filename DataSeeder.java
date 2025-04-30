package com.example.config;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner initRolesAndUsers(RoleRepository roleRepository, UserRepository userRepository) {
		return args -> {
			// Create roles if they don't exist
			Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
			Role userRole = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));

			// Create an admin user if not exists
			Optional<User> existingAdmin = userRepository.findByUsername("admin");
			if (existingAdmin.isEmpty()) {
				User admin = new User("admin", "{noop}admin123", Set.of(adminRole));
				userRepository.save(admin);
				System.out.println("✅ Admin user created: admin / admin123");
			}

			// Create a regular user if not exists
			Optional<User> existingUser = userRepository.findByUsername("testuser");
			if (existingUser.isEmpty()) {
				User user = new User("testuser", "{noop}user123", Set.of(userRole));
				userRepository.save(user);
				System.out.println("✅ Regular user created: testuser / user123");
			}
		};
	}
}
