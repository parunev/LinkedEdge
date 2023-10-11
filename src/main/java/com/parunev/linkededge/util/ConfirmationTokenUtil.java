package com.parunev.linkededge.util;

import com.parunev.linkededge.model.ConfirmationToken;
import com.parunev.linkededge.security.exceptions.AuthServiceException;
import com.parunev.linkededge.security.payload.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfirmationTokenUtil {

    private static final LELogger LE_LOGGER = new LELogger(ConfirmationTokenUtil.class);

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
