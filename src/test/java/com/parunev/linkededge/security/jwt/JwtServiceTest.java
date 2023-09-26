package com.parunev.linkededge.security.jwt;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.enums.Authority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @Mock
    private UserDetails userDetails;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private User userMock;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTokenUser() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        Map<String, Object> claims = new HashMap<>();
        claims.put("claims1", "value1");
        claims.put("claims2", "value2");
        Jwt expectedJwt = new Jwt("mocked-token-value", null, null, headers, claims);

        when(userMock.getAuthorities()).thenReturn(Collections.singleton(Authority.AUTHORITY_USER));
        when(userMock.getEmail()).thenReturn("test@example.com");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(expectedJwt);

        String token = jwtService.generateToken(userMock);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(token, expectedJwt.getTokenValue());
    }

    @Test
    void testExtractEmail() {
        String email = "test@example.com";
        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("claims2", "value2");

        Jwt expectedJwt = new Jwt("mocked-token-value", null, null, headers, claims);
        when(jwtDecoder.decode(any(String.class))).thenReturn(expectedJwt);

        String extractedEmail = jwtService.extractEmail(expectedJwt.getTokenValue());

        assertEquals(email, extractedEmail);
    }

    @Test
    void testIsTokenValid() {
        String email = "test@example.com";
        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("claims2", "value2");

        long jwtExpiration = 3600000;
        Jwt token = new Jwt("mocked-token-value", Instant.now(),
                Instant.now().plus(Duration.ofMillis(jwtExpiration)), headers, claims);

        when(userDetails.getUsername()).thenReturn(email);
        when(jwtDecoder.decode(any(String.class))).thenReturn(token);

        assertTrue(jwtService.isTokenValid(token.getTokenValue(), userDetails));
    }

    @Test
    void testGenerateRefreshTokenUser() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        Map<String, Object> claims = new HashMap<>();
        claims.put("claims1", "value1");
        claims.put("claims2", "value2");
        Jwt expectedJwt = new Jwt("mocked-refresh-token-value", null, null, headers, claims);

        when(userMock.getAuthorities()).thenReturn(Collections.singleton(Authority.AUTHORITY_USER));
        when(userMock.getEmail()).thenReturn("test@example.com");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(expectedJwt);

        String token = jwtService.generateRefreshToken(userMock);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(token, expectedJwt.getTokenValue());
    }
}
