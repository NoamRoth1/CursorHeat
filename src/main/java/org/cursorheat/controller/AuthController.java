package org.cursorheat.controller;

import org.cursorheat.model.User;
import org.cursorheat.security.CurrentUser;
import org.cursorheat.security.JwtService;
import org.cursorheat.service.AuthenticationService;
import org.cursorheat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling authentication-related endpoints.
 * 
 * This controller provides REST endpoints for user authentication operations including:
 * - User registration
 * - User login
 * - Current user information retrieval
 * - User logout
 *
 * The controller integrates with Spring Security and uses JWT for authentication.
 * All endpoints are prefixed with "/api/v1/auth".
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, UserService userService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Record representing a registration request.
     *
     * @param email The user's email address
     * @param password The user's password
     * @param firstName The user's first name
     * @param lastName The user's last name
     */
    public record RegistrationRequest(
        String email,
        String password,
        String firstName,
        String lastName
    ) {}

    /**
     * Record representing a login request.
     *
     * @param email The user's email address
     * @param password The user's password
     */
    public record LoginRequest(
        String email,
        String password
    ) {}

    /**
     * Record representing an authentication response.
     *
     * @param token The JWT token
     */
    public record AuthResponse(
        String token
    ) {}

    /**
     * Registers a new user in the system.
     *
     * @param request The registration request containing user details
     * @return A response containing the JWT token for the new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest request) {
        if (userService.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        String token = authenticationService.register(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request The login request containing user credentials
     * @return A response containing the JWT token for the authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authenticationService.login(request.email(), request.password());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Retrieves the current authenticated user's information.
     *
     * @return The current user's information
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        String username = CurrentUser.getUsername();
        logger.debug("/me endpoint called. Current username: {}", username);
        if (username == null) {
            logger.warn("/me endpoint: No authenticated user found.");
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByEmail(username);
        if (user == null) {
            logger.warn("/me endpoint: User not found for username: {}", username);
            return ResponseEntity.notFound().build();
        }
        logger.debug("/me endpoint: User found: {} (enabled: {})", user.getEmail(), user.isEnabled());
        return ResponseEntity.ok(user);
    }

    private Map<String, Object> createAuthResponse(User user, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", createUserResponse(user));
        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("role", user.getRole());
        userResponse.put("emailVerified", user.isEmailVerified());
        return userResponse;
    }
} 