package org.cursorheat.repository;

import org.cursorheat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity persistence operations.
 * 
 * This repository provides methods for:
 * - Basic CRUD operations on User entities
 * - Custom queries for user retrieval
 * - Email-based user lookup
 * - Email existence verification
 *
 * The repository extends JpaRepository to inherit standard JPA operations
 * and adds custom methods specific to user management.
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return An Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return An Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists.
     *
     * @param username The username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);
} 