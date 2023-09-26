package com.parunev.linkededge.security.mfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class Google2FATest {

    @InjectMocks
    private Google2FA google2FAuthentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateNewSecret() {
        String secret = google2FAuthentication.generateNewSecret();
        assertNotNull(secret);
    }

    @Test
    void testGenerateQrCodeImageUri() {
        String secret = google2FAuthentication.generateNewSecret();
        String qrCodeImageUri = google2FAuthentication.generateQrCodeImageUri(secret);

        assertNotNull(qrCodeImageUri);
        assertTrue(qrCodeImageUri.startsWith("data:image/png;base64,"));
    }

    @Test
    void testIsOtpValid() {
        String secret = google2FAuthentication.generateNewSecret();
        google2FAuthentication.generateQrCodeImageUri(secret);

        assertFalse(google2FAuthentication.isOtpValid(secret, "123456"));
    }
}
