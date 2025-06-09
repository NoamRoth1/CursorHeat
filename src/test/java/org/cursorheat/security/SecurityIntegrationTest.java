package org.cursorheat.security;

import org.cursorheat.model.User;
import org.cursorheat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        // mockMvc.perform(get("/about"))
        //         .andExpect(status().isOk()); // Removed as /about is not a defined endpoint

        // mockMvc.perform(get("/pricing"))
        //         .andExpect(status().isOk()); // Removed as /pricing is not a defined endpoint
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/account"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoints_ShouldRequireAdminRole() throws Exception {
        // Create a regular user
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setRole(User.Role.USER);
        userRepository.save(user);

        // Create an admin user
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        // Test admin endpoint access
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void projectEndpoints_ShouldRequireAuthentication() throws Exception {
        // Create a user
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setRole(User.Role.USER);
        userRepository.save(user);

        // Test project endpoint access
        mockMvc.perform(get("/project/1/edit"))
                .andExpect(status().isUnauthorized());
    }
} 