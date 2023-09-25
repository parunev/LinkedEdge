package com.parunev.linkededge.security.mfa;

import com.google.common.cache.LoadingCache;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.payload.login.VerificationRequest;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.OTPValidationException;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
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

@Component
@RequiredArgsConstructor
public class Email2FA {
    private final LoadingCache<String, Integer> otpCache;
    private final UserRepository userRepository;
    private final LELogger leLogger = new LELogger(Email2FA.class);
    private final EmailSender emailSender;
    private final Random random = new Random();

    public void sendOtp(final User user, final String subject) {

        try {
            leLogger.info("Removing OTP from cache");
            otpCache.get(user.getEmail());
            otpCache.invalidate(user.getEmail());
        } catch (ExecutionException e) {
            leLogger.error("Failed to fetch pair from OTP cache: {}", e);
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
        }

        final var otp = generateRandomOtp();
        otpCache.put(user.getEmail(), otp);

        CompletableFuture.supplyAsync(() -> {
            leLogger.info("Sending OTP to user");
            emailSender.send(user.getEmail(), subject, "OTP: " + otp);
            return HttpStatus.OK;
        });
    }

    private int generateRandomOtp() {
        return random.ints(1, 100000, 999999).sum();
    }

    public boolean verifyOtp(final VerificationRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided username not found: {}", request.getUsername());
                    throw new UserNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("User with the provided username not found. Please ensure you have entered the correct username.")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                }
        );

        if (user == null) {
            throw new OTPValidationException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Failed to fetch userfrom database.")
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }

        Integer storedOneTimePassword;

        try{
            storedOneTimePassword = otpCache.get(user.getEmail());
        } catch (ExecutionException e) {
            throw new OTPValidationException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Failed to fetch OTP from cache.")
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .build());
        }

        return storedOneTimePassword.equals(Integer.parseInt(request.getCode()));
    }

}
