package com.parunev.linkededge.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @Description: This class is responsible for providing the current auditor (user) when using Spring Data JPA auditing features.
 * It implements the Spring Data JPA `AuditorAware` interface to fetch the username of the current user.
 * If there is no authenticated user, it falls back to using "LINKED_EDGE" as the default auditor.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    /**
     * Retrieves the current auditor (user) if available, or falls back to "LINKED_EDGE" as the default.
     *
     * @return An optional containing the username of the current user, or "LINKED_EDGE" as the default.
     */
    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null){
            String username = authentication.getName();
            return Optional.ofNullable(username).filter(s -> !s.isEmpty());
        } else {
            return Optional.of("LINKED_EDGE");
        }
    }
}
