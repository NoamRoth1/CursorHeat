package org.cursorheat.service;

import org.cursorheat.model.User;
import org.cursorheat.repository.UserRepository;
import org.cursorheat.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling user authentication operations including registration and login.
 * 
 * This service provides the core authentication functionality for the application, managing user
 * registration, login, and token generation. It integrates with Spring Security's authentication
 * manager and uses JWT for secure token-based authentication.
 *
 * The service ensures that:
 * - User passwords are properly encoded before storage
 * - Authentication is performed securely using Spring Security
 * - JWT tokens are generated for authenticated users
 * - User roles and permissions are properly managed
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user in the system.
     * 
     * This method performs the following steps:
     * 1. Checks if the email is already registered
     * 2. Encodes the user's password
     * 3. Saves the user to the database
     * 4. Generates a JWT token for the new user
     *
     * @param user The user to register
     * @return A JWT token for the newly registered user
     * @throws RuntimeException if the email is already registered
     */
    public String register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * This method performs the following steps:
     * 1. Authenticates the user using Spring Security's AuthenticationManager
     * 2. Retrieves the user details from the database
     * 3. Generates a JWT token for the authenticated user
     *
     * @param email The email of the user to authenticate
     * @param password The password of the user to authenticate
     * @return A JWT token for the authenticated user
     * @throws RuntimeException if authentication fails
     */
    public String login(String email, String password) {
        try {
            authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, password)
            );
            var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return jwtService.generateToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password", e);
        }
    }
} 