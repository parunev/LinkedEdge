package com.parunev.linkededge.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringSecurityAuditorAwareTest {
    @InjectMocks
    private SpringSecurityAuditorAware auditorAware;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void resetSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentAuditor_WhenUserIsAuthenticated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        assertEquals("testuser", currentAuditor.orElse(null));
    }

    @Test
    void testGetCurrentAuditor_WhenNoUserIsAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        assertEquals("LINKED_EDGE", currentAuditor.orElse(null));
    }
}
