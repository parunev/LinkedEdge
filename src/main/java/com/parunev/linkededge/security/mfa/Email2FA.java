package com.parunev.linkededge.security.mfa;

import com.google.common.cache.LoadingCache;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.payload.login.VerificationRequest;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.OTPValidationException;
import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * @Description: The `Email2FA` class provides Two-Factor Authentication (2FA) functionality using email-based OTP (One-Time Password) verification.
 * It allows users to receive OTPs through email and verify them during login.
 * This enhances security by requiring an additional authentication step.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
@RequiredArgsConstructor
public class Email2FA {
    private final LoadingCache<String, Integer> otpCache; // Loading cache for OTP storage
    private final UserRepository userRepository; // Repository to access user information
    private final LELogger leLogger = new LELogger(Email2FA.class);
    private final EmailSender emailSender; // Email sender for sending OTP emails
    private final Random random = new Random(); // Random number generator for OTP generation

    /**
     * Send an OTP to the user's email.
     *
     * @param user    The user for whom to send the OTP.
     * @param subject The subject of the email containing the OTP.
     */
    public void sendOtp(final User user, final String subject) {

        try {
            // Remove any existing OTP from the cache
            leLogger.info("Removing OTP from cache");
            otpCache.get(user.getEmail());
            otpCache.invalidate(user.getEmail());
        } catch (ExecutionException e) {
            leLogger.error("Failed to fetch pair from OTP cache: {}", e);
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
        }

        // Generate a random OTP
        final var otp = generateRandomOtp();
        // Store the OTP in the cache
        otpCache.put(user.getEmail(), otp);

        // Send the OTP to the user's email asynchronously
        CompletableFuture.supplyAsync(() -> {
            leLogger.info("Sending OTP to user");
            emailSender.send(user.getEmail(), subject, "OTP: " + otp);
            return HttpStatus.OK;
        });
    }

    /**
     * Generate a random OTP (One-Time Password).
     *
     * @return A random OTP.
     */
    private int generateRandomOtp() {
        return random.ints(1, 100000, 999999).sum();
    }

    /**
     * Verify the provided OTP against the stored OTP in the cache for a given user.
     *
     * @param request The verification request containing the username and OTP code.
     * @return `true` if the provided OTP matches the stored OTP, `false` otherwise.
     */
    public boolean verifyOtp(final VerificationRequest request){
        // Find the user by username
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided username not found: {}", request.getUsername());
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("User with the provided username not found. Please ensure you have entered the correct username.")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                }
        );

        Integer storedOneTimePassword;

        try{
            // Retrieve the stored OTP from the cache
            storedOneTimePassword = otpCache.get(user.getEmail());
        } catch (ExecutionException e) {
            throw new OTPValidationException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Failed to fetch OTP from cache.")
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .build());
        }

        // Compare the provided OTP with the stored OTP
        return storedOneTimePassword.equals(Integer.parseInt(request.getCode()));
    }

}
