package org.cursorheat.service;

import org.cursorheat.model.User;
import org.cursorheat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for managing user-related operations.
 * 
 * This service provides methods for:
 * - User creation and registration
 * - User retrieval and search
 * - User information updates
 * - Email existence verification
 * - Password management
 *
 * The service ensures that:
 * - User data is properly validated
 * - Passwords are securely encoded
 * - Database operations are transactional
 * - User information is consistently managed
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user with the provided information.
     *
     * @param email The user's email address
     * @param password The user's password (will be encoded)
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @return The created User object
     * @throws RuntimeException if the email is already registered
     */
    @Transactional
    public User createUser(String email, String password, String firstName, String lastName) {
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);
        user.setName(firstName + " " + lastName);
        user.setEmailVerified(false);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address to search for
     * @return An Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves a user by their email address, throwing an exception if not found.
     *
     * @param email The email address to search for
     * @return The found User object
     * @throws RuntimeException if the user is not found
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email address to check
     * @return true if a user with the email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Saves a user to the database.
     *
     * @param user The user to save
     * @return The saved User object
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates a user's password.
     *
     * @param user The user to update
     * @param newPassword The new password (will be encoded)
     * @return The updated User object
     */
    @Transactional
    public User updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Verifies a user's email address.
     *
     * @param user The user to verify
     * @return The updated User object
     */
    @Transactional
    public User verifyEmail(User user) {
        user.setEmailVerified(true);
        return userRepository.save(user);
    }
} 