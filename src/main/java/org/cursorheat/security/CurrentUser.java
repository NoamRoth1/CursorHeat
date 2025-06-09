package org.cursorheat.security;

import org.cursorheat.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing the currently authenticated user's information.
 * 
 * This class provides static methods to access the current user's information
 * from the Spring Security context. It is used throughout the application to
 * get the current user's details and check their roles.
 *
 * The class ensures that:
 * - User information is accessed consistently across the application
 * - Role checks are performed in a standardized way
 * - Null checks are handled appropriately
 *
 * @author CursorHeat Team
 * @version 1.0
 */
public class CurrentUser {

    private CurrentUser() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the username (email) of the currently authenticated user.
     *
     * @return The username of the current user, or null if not authenticated
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Checks if the current user has a specific role.
     *
     * @param role The role to check for (e.g., "ADMIN", "USER")
     * @return true if the user has the specified role, false otherwise
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Checks if the current user is an administrator.
     *
     * @return true if the user has the ADMIN role, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Gets the currently authenticated user object.
     *
     * @return The current User object, or null if not authenticated
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }
} 