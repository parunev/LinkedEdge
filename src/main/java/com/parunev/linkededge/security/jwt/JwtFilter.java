package com.parunev.linkededge.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.repository.JwtTokenRepository;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.service.UserService;
import com.parunev.linkededge.util.LELogger;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * @Description: This class represents a custom filter for handling JWT-based authentication in the LinkedEdge application.
 * It is responsible for processing JWT tokens, authenticating users, and enforcing security measures.
 * This filter is invoked on each incoming HTTP request to secure the application.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService; // Service for JWT token operations
    private final UserService userService; // Service for user-related operations
    private final JwtTokenRepository jwTokenRepository; // Repository for JWT tokens
    private final UserRepository userRepository; // Repository for user information
    private final ObjectMapper objectMapper; // ObjectMapper for JSON serialization/deserialization
    private final LELogger leLogger = new LELogger(JwtFilter.class); // Logger for monitoring filter activity
    private static final String CORRELATION_ID = "correlationId"; // Mapped Diagnostic Context key for correlation ID
    private static final String[] HEADERS = {"Authorization", "Bearer "}; // HTTP headers for JWT token extraction

    /**
     * Override of the `doFilterInternal` method from the `OncePerRequestFilter` class.
     * It processes each incoming HTTP request to apply JWT-based authentication.
     *
     * @param request       The incoming HTTP request
     * @param response      The HTTP response
     * @param filterChain   The filter chain for additional processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Set a unique correlation ID for this request
            MDC.put(CORRELATION_ID, LELogger.generateCorrelationId());
            LELogger.setLoggerProperties(MDC.get(CORRELATION_ID), request);

            // Log the details of the incoming request
            if(request.getRequestURI().contains("/edge-api/")){ // DISABLING THE SWAGGER LOGGING LEVELS
                leLogger.debug("Received request: {} {}",
                        request.getMethod(), request.getRequestURI());
            }

            final String authHeader = request.getHeader(HEADERS[0]);
            final String jwt;
            final String email;

            if (authHeader == null || !authHeader.startsWith(HEADERS[1])) {
                // No JWT token found in the request. Proceed without authentication.
                if(request.getRequestURI().contains("/edge-api/")){ // DISABLING THE SWAGGER LOGGING LEVELS
                    leLogger.debug("No JWT token found in the request. Proceeding without authentication.");
                }

                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7); // Extract JWT token from the authorization header
            email = jwtService.extractEmail(jwt); // Extract email from the JWT token

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Retrieve user details based on the email
                User user = findUserByEmail(email);
                UserDetails userDetails = this.userService.loadUserByUsername(user.getUsername());

                // Check if the JWT token is valid and not revoked
                boolean isTokenValid = jwTokenRepository.findByTokenValue(jwt)
                        .map(jwToken -> !jwToken.isExpired() && !jwToken.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    // User authentication is successful
                    leLogger.info("User {} authenticated successfully.", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the user's authentication details in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                // Continue with the filter chain
                filterChain.doFilter(request, response);
            }
        } catch (SignatureException | ExpiredJwtException | ResourceNotFoundException exception) {
            // Handle authentication failures
            leLogger.error("Authentication failed: {}", exception, exception.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(ApiError.builder()
                    .path(request.getRequestURI())
                    .error(exception.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .timestamp(LocalDateTime.now())
                    .build()));

        } finally {
            // Remove the correlation ID and clear logger properties
            MDC.remove(CORRELATION_ID);
            LELogger.clearLoggerProperties();
        }
    }

    /**
     * Find a user by email and throw a ResourceNotFoundException if not found.
     *
     * @param email The email address of the user
     * @return The user with the given email
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided email not found: {}", email);
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("User with the provided email not found. Please ensure you have created an account")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                }
        );
    }
}
