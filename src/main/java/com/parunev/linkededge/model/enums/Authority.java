package com.parunev.linkededge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

/**
 * The `Authority` enum represents the various roles and authorities in the LinkedEdge application. It implements the `GrantedAuthority` interface
 * to provide authority information for authentication and authorization purposes.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Getter
@AllArgsConstructor
public enum Authority implements GrantedAuthority {

    /**
     * The standard user authority.
     */
    AUTHORITY_USER("ROLE_USER"),

    /**
     * Additional user authority with extra privileges.
     */
    AUTHORITY_USER_EXTRA("ROLE_USER_EXTRA"),

    /**
     * Administrative authority.
     */
    AUTHORITY_ADMIN("ROLE_ADMIN");

    /**
     * The name of the authority.
     */
    private final String authorityName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthority() {
        return this.authorityName;
    }
}
