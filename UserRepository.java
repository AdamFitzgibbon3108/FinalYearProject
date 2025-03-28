package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u.recommendedSecurityCategory FROM User u WHERE u.username = :username")
    Optional<String> findRecommendedSecurityCategoryByUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.recommendedSecurityCategory = :category WHERE u.username = :username")
    void updateRecommendedSecurityCategory(@Param("username") String username, @Param("category") String category);

    // Fetch user along with roles
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    // Fetch all users along with their roles
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();

    // Count users by role
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRole(@Param("roleName") String roleName);

    // Count active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();

    // Count users pending approval
    @Query("SELECT COUNT(u) FROM User u WHERE u.pendingApproval = true")
    long countPendingUsers();

    // Ban user
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.banned = true WHERE u.id = :userId")
    void banUserById(@Param("userId") Long userId);

    // Unban user
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.banned = false WHERE u.id = :userId")
    void unbanUserById(@Param("userId") Long userId);

    // List all banned users
    @Query("SELECT u FROM User u WHERE u.banned = true")
    List<User> findAllBannedUsers();
    
 // Auto-generated Spring query for banned users
    List<User> findByBannedTrue();
    
    List<User> findByBannedFalse(); // ✅ For active users

    

}
