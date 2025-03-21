package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the specified role and redirects them to the appropriate dashboard.
     * @param user The user to register
     * @param roleName The role to assign
     * @return RedirectView to the correct dashboard
     */
    public RedirectView registerUser(User user, String roleName) {
        logger.info("📝 Attempting to register user: {}", user.getUsername());

        //  Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("❌ Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists!");
        }

        //  Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //  Assign role dynamically based on input
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> {
                    logger.error("❌ Role '{}' does not exist!", roleName);
                    return new IllegalArgumentException("Role does not exist!");
                });

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
        logger.info("✅ User '{}' registered successfully with role '{}'", user.getUsername(), roleName);

        //  Redirect user to the appropriate dashboard
        RedirectView redirectView = getRedirectView(roleName);
        logger.info("🔄 Redirecting '{}' to '{}'", user.getUsername(), redirectView.getUrl());
        return redirectView;
    }

    /**
     * Registers a new admin. Only an existing admin can create a new admin.
     * @param user The admin to register
     * @return RedirectView to the correct dashboard
     */
    @Transactional
    public RedirectView registerAdmin(User user) {
        logger.info("📝 Attempting to register admin: {}", user.getUsername());

        //  Step 1: Check if the username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("❌ Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists!");
        }
        logger.info("✅ Username '{}' is available", user.getUsername());

        //  Step 2: Ensure the request is made by an authenticated ADMIN user
        if (!isAuthenticatedUserAdmin()) {
            logger.error("🚨 Unauthorized attempt to create admin by non-admin user.");
            throw new SecurityException("Only admins can create new admin accounts.");
        }
        logger.info("✅ Authenticated user is an admin, proceeding with registration.");

        //  Step 3: Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("✅ Password encoded for '{}'", user.getUsername());

        //  Step 4: Retrieve ADMIN role
        Optional<Role> optionalAdminRole = roleRepository.findByName("ADMIN");
        if (optionalAdminRole.isEmpty()) {
            logger.error("❌ ADMIN role does not exist! Ensure roles are properly seeded.");
            throw new IllegalArgumentException("ADMIN role does not exist!");
        }
        Role adminRole = optionalAdminRole.get();
        logger.info("✅ Retrieved role: {}", adminRole.getName());

        //  Step 5: Assign only the ADMIN role
        user.getRoles().clear(); // Ensure no previous roles exist
        user.getRoles().add(adminRole);
        logger.info("✅ Assigned ADMIN role to '{}'", user.getUsername());

        //  Step 6: Save user with ADMIN role
        user = userRepository.save(user);
        logger.info("✅ Admin '{}' registered successfully!", user.getUsername());

        //  Step 7: Fetch the user again to verify roles
        Optional<User> savedUser = userRepository.findByUsernameWithRoles(user.getUsername());
        if (savedUser.isEmpty()) {
            logger.error("❌ Failed to retrieve user '{}' after saving!", user.getUsername());
            throw new IllegalStateException("User retrieval failed after saving.");
        }
        logger.info("🔎 User '{}' retrieved from DB after save. Assigned roles: {}", 
            savedUser.get().getUsername(), savedUser.get().getRoles());

        //  Step 8: Verify if the user has ADMIN role
        boolean isAdminAssigned = savedUser.get().getRoles().stream()
            .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isAdminAssigned) {
            logger.error("❌ User '{}' does NOT have ADMIN role after saving!", user.getUsername());
            throw new IllegalStateException("Role assignment failed for admin registration!");
        }
        logger.info("✅ User '{}' successfully assigned ADMIN role!", user.getUsername());

        //  Step 9: Redirect admin to the admin dashboard
        return new RedirectView("/admin-dashboard");
    }




    /**
     * Checks if the currently logged-in user has the "ADMIN" role.
     * @return true if the user is an admin, false otherwise
     */
    private boolean isAuthenticatedUserAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
            boolean isAdmin = user.isPresent() && user.get().getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

            if (isAdmin) {
                logger.info("✅ Authenticated user '{}' has ADMIN role", userDetails.getUsername());
            } else {
                logger.warn("🚫 Authenticated user '{}' does NOT have ADMIN role", userDetails.getUsername());
            }
            return isAdmin;
        }

        logger.warn("🚫 Authentication principal is not an instance of UserDetails");
        return false;
    }

    /**
     * Returns a RedirectView to the appropriate dashboard based on the role.
     * @param roleName The assigned role
     * @return RedirectView to the correct dashboard
     */
    private RedirectView getRedirectView(String roleName) {
        if ("ADMIN".equalsIgnoreCase(roleName)) {
            return new RedirectView("/admin-dashboard");
        }
        return new RedirectView("/dashboard");
    }
}

