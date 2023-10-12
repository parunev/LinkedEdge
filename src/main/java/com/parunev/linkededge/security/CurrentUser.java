package com.parunev.linkededge.security;

import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * The `CurrentUser` class provides a utility for retrieving the details of the currently authenticated user.
 * It's a component designed for easy access to user details.
 * <p>
 * Note:
 * - This class is a component and should not be instantiated. It provides a static method to retrieve user details.
 * - If no authentication is found, a `ResourceNotFoundException` is thrown with an `ApiError` payload indicating the absence of authentication.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrentUser {

    /**
     * Retrieves the `UserDetails` of the currently authenticated user.
     *
     * @return The `UserDetails` of the currently authenticated user.
     * @throws ResourceNotFoundException if no authentication is presented.
     */
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        throw new ResourceNotFoundException(ApiError
                .builder()
                .error("No authentication presented")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED)
                .build());
    }
}
