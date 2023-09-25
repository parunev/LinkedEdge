package com.parunev.linkededge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum Authority implements GrantedAuthority {
    AUTHORITY_USER("ROLE_USER"),
    AUTHORITY_USER_EXTRA("ROLE_USER_EXTRA"),
    AUTHORITY_ADMIN("ROLE_ADMIN");

    private final String authorityName;

    @Override
    public String getAuthority() {
        return this.authorityName;
    }
}
