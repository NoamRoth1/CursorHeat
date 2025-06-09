package org.cursorheat.service;

import org.cursorheat.model.User;
import org.cursorheat.repository.UserRepository;
import org.cursorheat.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "testsecretkeytestsecretkeytestsecretkeytestsecretkey");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        authenticationService = new AuthenticationService(
            userRepository,
            passwordEncoder,
            jwtService,
            authenticationManager
        );
    }

    @Test
    void register_WithNewUser_ShouldSucceed() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        String token = authenticationService.register(user);

        // Assert
        assertNotNull(token);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.register(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        String token = authenticationService.login(email, password);

        // Assert
        assertNotNull(token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any()))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.login(email, password));
    }
} 