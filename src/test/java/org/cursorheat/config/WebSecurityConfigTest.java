package org.cursorheat.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // Define member-only and public pages based on prior confirmation
    private static final String[] MEMBER_PAGES = {
            "/dashboard.html",
            "/account.html",
            "/billing.html",
            "/profile.html",
            "/projects.html",
            "/project-detail.html",
            "/project-edit.html",
            "/settings.html",
            "/support.html"
    };

    private static final String[] PUBLIC_PAGES = {
            "/",
            "/index.html",
            "/login.html",
            "/signup.html",
            "/docs.html",
            "/error.html"
            // Static assets like /css/**, /js/** are covered by permitAll in WebSecurityConfig
            // and usually don't need direct HTML page tests unless they serve HTML content.
    };

    @Test
    public void whenUnauthenticated_thenMemberPagesRedirectToLogin() throws Exception {
        for (String page : MEMBER_PAGES) {
            mockMvc.perform(get(page))
                   .andExpect(status().isFound()) // Expect 302 Found (redirect)
                   .andExpect(redirectedUrlPattern("**/login.html")); // Check if redirects to login
        }
    }

    @Test
    @WithMockUser // Simulates an authenticated user
    public void whenAuthenticated_thenMemberPagesAreAccessible() throws Exception {
        for (String page : MEMBER_PAGES) {
            mockMvc.perform(get(page))
                   .andExpect(status().isOk());
        }
    }

    @Test
    public void whenUnauthenticated_thenPublicPagesAreAccessible() throws Exception {
        for (String page : PUBLIC_PAGES) {
            mockMvc.perform(get(page))
                   .andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser // Simulates an authenticated user
    public void whenAuthenticated_thenPublicPagesAreAccessible() throws Exception {
        for (String page : PUBLIC_PAGES) {
            mockMvc.perform(get(page))
                   .andExpect(status().isOk());
        }
    }

    // Test for static assets if necessary, though covered by broad patterns
    @Test
    public void whenUnauthenticated_thenStaticAssetsAreAccessible() throws Exception {
        mockMvc.perform(get("/css/styles.css")) // Corrected path
               .andExpect(status().isOk());
        mockMvc.perform(get("/js/cursor-heat.js"))  // Corrected path
               .andExpect(status().isOk());
    }
}
