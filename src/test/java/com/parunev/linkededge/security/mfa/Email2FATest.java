package com.parunev.linkededge.security.mfa;

import com.google.common.cache.LoadingCache;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.payload.login.VerificationRequest;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
import com.parunev.linkededge.util.email.EmailSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class Email2FATest {

    @Mock
    private LoadingCache<String, Integer> otpCache;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSender emailSender;

    @Spy
    private Random random = new Random();

    @InjectMocks
    private Email2FA email2FA;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/edge-api/v1/auth")
                .buildRequest(new MockServletContext());

        ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);

        RequestContextHolder.setRequestAttributes(mockAttributes);

        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void resetCache() {
        otpCache.cleanUp();
    }

    @Test
    void testSendOtp_User() throws ExecutionException {
        User user = new User();
        user.setEmail("user@example.com");

        when(otpCache.get("user@example.com")).thenReturn(123456);
        doNothing().when(emailSender).send(anyString(), anyString(), anyString());

        email2FA.sendOtp(user, "OTP Subject");

        verify(otpCache).get("user@example.com");
        verify(otpCache).invalidate("user@example.com");
    }

    @Test
    void testSendOtp_Exception() throws ExecutionException {
        User user = new User();
        user.setEmail("user@example.com");

        when(otpCache.get("user@example.com")).thenThrow(new ExecutionException("Error", new RuntimeException()));

        assertThrows(ResponseStatusException.class, () -> email2FA.sendOtp(user, "OTP Subject"));

        verify(otpCache).get("user@example.com");
        verifyNoMoreInteractions(otpCache);
        verifyNoInteractions(emailSender);
    }

    @Test
    void testVerifyOtp_ValidOtp() throws ExecutionException {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("123456");

        User user = new User();
        user.setUsername("username");
        user.setEmail("user@example.com");

        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Integer storedOneTimePassword = 123456;
        when(otpCache.get("user@example.com")).thenReturn(storedOneTimePassword);

        boolean result = email2FA.verifyOtp(request);

        assertTrue(result);
        verify(userRepository).findByUsername("username");
        verify(otpCache).get("user@example.com");
    }

    @Test
    void testVerifyOtp_InvalidOtp() throws ExecutionException {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("654321");

        User user = new User();
        user.setUsername("username");
        user.setEmail("user@example.com");

        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Integer storedOneTimePassword = 123456;
        when(otpCache.get("user@example.com")).thenReturn(storedOneTimePassword);

        boolean result = email2FA.verifyOtp(request);

        assertFalse(result);
        verify(userRepository).findByUsername("username");
        verify(otpCache).get("user@example.com");
    }

    @Test
    void testVerifyOtp_UserNotFound() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("123456");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> email2FA.verifyOtp(request));

        verify(userRepository).findByUsername(request.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(otpCache);
    }

}
