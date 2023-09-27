package com.parunev.linkededge.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parunev.linkededge.model.JwtToken;
import com.parunev.linkededge.repository.JwtTokenRepository;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JwtFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled(value = "Further testing needed")
    void testDoFilterInternal_WhenJwtIsValid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtService.extractEmail("valid-jwt-token")).thenReturn("user@example.com");
        when(userService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtTokenRepository.findByTokenValue("valid-jwt-token")).thenReturn(Optional.of(new JwtToken()));
        when(jwtService.isTokenValid("valid-jwt-token", userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userDetails).getUsername();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WhenExceptionIsCaught() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-jwt-token");

        when(jwtService.extractEmail("invalid-jwt-token")).thenAnswer(invocation -> {
            throw new JwtException("Invalid token");
        });

        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenRepository.findByTokenValue("invalid-jwt-token")).thenReturn(Optional.empty());

        assertThrows(JwtException.class, () ->
                jwtFilter.doFilterInternal(request, response, filterChain));

        verifyNoInteractions(filterChain);
    }
}
