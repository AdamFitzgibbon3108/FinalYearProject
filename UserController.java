package com.example.controller;

import com.example.model.Role;
import com.example.model.User;
import com.example.service.UserService;
import com.example.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    //  ADMIN-ONLY: Get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //  Users can view their own profile, but only ADMIN can view any user
    @PreAuthorize("hasRole('ADMIN') or #principal.name == authentication.name")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    //  ADMIN-ONLY: Create a new user (Admin or Normal User)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User createUser(@RequestBody User user) {
        // Ensure at least "USER" role is assigned
        Set<Role> roles = new HashSet<>();
        
        // Convert role names to Role objects
        for (Role role : user.getRoles()) {
            Optional<Role> foundRole = roleRepository.findByName(role.getName());
            if (foundRole.isPresent()) {
                roles.add(foundRole.get());
            } else {
                throw new RuntimeException("Role not found: " + role.getName());
            }
        }

        user.setRoles(roles);
        return userService.createUser(user);
    }

    //  ADMIN-ONLY: Update user details
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    //  ADMIN-ONLY: Delete a user
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

