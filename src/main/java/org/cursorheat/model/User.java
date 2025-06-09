package org.cursorheat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity class representing a user in the system.
 * 
 * This class implements Spring Security's UserDetails interface to integrate with
 * Spring Security's authentication and authorization system. It contains all necessary
 * user information including authentication details, personal information, and role-based
 * access control.
 *
 * The class includes:
 * - Basic user information (name, email, username)
 * - Authentication details (password, enabled status)
 * - Role-based access control
 * - Personal information (first name, last name)
 * - Email verification status
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * Enum representing the possible roles a user can have in the system.
     */
    public enum Role {
        USER,
        ADMIN
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email and automatically sets the username to match the email.
     *
     * @param email The email to set
     */
    public void setEmail(String email) {
        this.email = email;
        this.username = email; // Username is set to email by default
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name and updates the full name.
     *
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateName();
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name and updates the full name.
     *
     * @param lastName The last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateName();
    }

    /**
     * Updates the user's full name based on first and last name.
     */
    private void updateName() {
        if (firstName != null && lastName != null) {
            this.name = firstName + " " + lastName;
        } else if (firstName != null) {
            this.name = firstName;
        } else if (lastName != null) {
            this.name = lastName;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
} 