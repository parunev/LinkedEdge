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

@Component
@NoArgsConstructor(access = AccessLevel.NONE)
public class CurrentUser {

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
