package com.parunev.linkededge.security.jwt;

import com.parunev.linkededge.model.JwtToken;
import com.parunev.linkededge.repository.JwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * @Description: The `JwtLogout` class is responsible for handling user logout actions,
 * particularly for invalidating or revoking JWT tokens during logout. It implements the `LogoutHandler`
 * interface to perform the logout operation.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Service
@RequiredArgsConstructor
public class JwtLogout implements LogoutHandler {

    private final JwtTokenRepository jwtTokenRepository; // Repository for JWT tokens

    /**
     * This method performs the user logout by invalidating and revoking the JWT token associated with the user's session. It is invoked when a user logs out.
     *
     * @param request         The HTTP request object
     * @param response        The HTTP response object
     * @param authentication   The authentication object, which contains user details
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            // No JWT token found in the request. Proceed without further action.
            return;
        }

        jwt = authHeader.substring(7); // Extract the JWT token from the Authorization header
        JwtToken storedToken = jwtTokenRepository.findByTokenValue(jwt)
                .orElse(null);

        if (storedToken != null) {
            // Mark the stored token as expired and revoked
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            jwtTokenRepository.save(storedToken); // Update the token in the repository
        }
    }
}
