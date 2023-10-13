package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * The `User` class represents a user entity in the LinkedEdge application.
 * It extends the `BaseEntity` class and implements the `UserDetails` interface
 * to provide user-related information and security details.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EDGE_USERS")
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "USER_ID"))
public class User extends BaseEntity implements UserDetails {

    /**
     * The username of the user.
     */
    @Column(name = "USERNAME", length = 100, nullable = false)
    private String username;

    /**
     * The email address of the user.
     */
    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    /**
     * The user's password.
     */
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    /**
     * The user's first name.
     */
    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(name = "LAST_NAME", length = 100, nullable = false)
    private String lastName;

    /**
     * The user's LinkedIn profile URL.
     */
    @Column(name = "LINKED_IN_PROFILE", nullable = false)
    private String linkedInProfile;

    /**
     * The authority level of the user (e.g., AUTHORITY_USER, AUTHORITY_USER_EXTRA, AUTHORITY_ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "AUTHORITY", nullable = false)
    private Authority authority;

    /**
     * Indicates whether the user account is enabled (default is false).
     */
    @Column(name = "ENABLED", columnDefinition = "boolean default false")
    private boolean isEnabled;

    /**
     * Indicates whether multi-factor authentication (MFA) is enabled for the user (default is false).
     */
    @Column(name = "MFA_ENABLED", columnDefinition = "boolean default false")
    private boolean mfaEnabled;

    /**
     * The secret used for multi-factor authentication (MFA) for the user.
     */
    @Column(name = "MFA_SECRET", nullable = false)
    private String mfaSecret;

    /**
     * Returns a collection of granted authorities for the user.
     *
     * @return A collection of granted authorities, with the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(authority.getAuthorityName()));
    }

    /**
     * Checks if the user account is non-expired.
     *
     * @return `true` if the account is non-expired, `false` otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {return true;}

    /**
     * Checks if the user account is non-locked.
     *
     * @return `true` if the account is non-locked, `false` otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {return true;}

    /**
     * Checks if the user credentials are non-expired.
     *
     * @return `true` if the credentials are non-expired, `false` otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {return true;}

    /**
     * Retrieves the user's full name by concatenating the first name and last name.
     *
     * @return The user's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
