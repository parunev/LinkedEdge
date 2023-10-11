package com.parunev.linkededge.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

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
