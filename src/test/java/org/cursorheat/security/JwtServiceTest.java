package org.cursorheat.security;

import org.cursorheat.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "testsecretkeytestsecretkeytestsecretkeytestsecretkey");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldReturnEmail() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        assertEquals(testUser.getEmail(), username);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        String token = "invalid.token.here";
        assertFalse(jwtService.isTokenValid(token, testUser));
    }
} 