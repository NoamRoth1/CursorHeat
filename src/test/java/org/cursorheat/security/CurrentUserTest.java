package org.cursorheat.security;

import org.cursorheat.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUsername_WhenNotAuthenticated_ShouldReturnNull() {
        assertNull(CurrentUser.getUsername());
    }

    @Test
    void getUsername_WhenAuthenticated_ShouldReturnUsername() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        String username = CurrentUser.getUsername();

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void hasRole_WhenNotAuthenticated_ShouldReturnFalse() {
        assertFalse(CurrentUser.hasRole("ADMIN"));
    }

    @Test
    void hasRole_WhenAuthenticatedWithRole_ShouldReturnTrue() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(User.Role.ADMIN);
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, Collections.singletonList(() -> "ROLE_ADMIN")
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertTrue(CurrentUser.hasRole("ADMIN"));
        assertFalse(CurrentUser.hasRole("USER"));
    }

    @Test
    void isAdmin_WhenUserIsAdmin_ShouldReturnTrue() {
        // Arrange
        User user = new User();
        user.setEmail("admin@example.com");
        user.setRole(User.Role.ADMIN);
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, Collections.singletonList(() -> "ROLE_ADMIN")
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertTrue(CurrentUser.isAdmin());
    }
} 