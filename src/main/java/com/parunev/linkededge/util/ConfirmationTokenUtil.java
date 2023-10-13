package com.parunev.linkededge.util;

import com.parunev.linkededge.model.ConfirmationToken;
import com.parunev.linkededge.security.exceptions.AuthServiceException;
import com.parunev.linkededge.security.payload.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * The `ConfirmationTokenUtil` class provides utility methods for handling confirmation tokens used for user registration
 * and account-related actions. It ensures that confirmation tokens are valid and not expired.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfirmationTokenUtil {

    private static final LELogger LE_LOGGER = new LELogger(ConfirmationTokenUtil.class);

    /**
     * Validates the provided confirmation token to ensure it is not already confirmed and has not expired.
     *
     * @param confirmationToken The confirmation token to be validated.
     * <p>
     * This method checks whether the provided confirmation token has already been confirmed or has expired. If either
     * condition is met, it throws an `AuthServiceException` with a corresponding error message, status, and timestamp.
     */
    public static void isValidToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmed() != null) {
            LE_LOGGER.warn("Token already confirmed: {}", confirmationToken.getParameters());
            throw new AuthServiceException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("The provided token has already been confirmed")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        if (confirmationToken.getExpires().isBefore(LocalDateTime.now())) {
            LE_LOGGER.warn("Token has expired: {}", confirmationToken.getParameters());
            throw new AuthServiceException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("The provided token has expired. Please request a new one")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }
}
