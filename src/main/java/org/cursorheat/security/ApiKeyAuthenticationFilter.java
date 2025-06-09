package org.cursorheat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cursorheat.model.Project;
import org.cursorheat.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ProjectRepository projectRepository;

    public ApiKeyAuthenticationFilter(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String apiKey = request.getHeader("X-API-Key");

            if (apiKey == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Project project = projectRepository.findByApiKey(apiKey)
                    .orElse(null);

            if (project != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    project,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid API key");
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("API key authentication failed");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        System.out.println("[ApiKeyAuthenticationFilter] shouldNotFilter path: " + path);
        return path.equals("/health") || 
               path.startsWith("/api/v1/auth") || 
               path.equals("/login.html") || 
               path.equals("/signup.html") ||
               path.equals("/index.html") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/static/");
    }
} 