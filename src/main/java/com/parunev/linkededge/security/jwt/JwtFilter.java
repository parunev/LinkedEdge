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


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final JwtTokenRepository jwTokenRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final LELogger leLogger = new LELogger(JwtFilter.class);
    private static final String CORRELATION_ID = "correlationId";
    private static final String[] HEADERS = {"Authorization", "Bearer "};


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            MDC.put(CORRELATION_ID, LELogger.generateCorrelationId());
            LELogger.setLoggerProperties(MDC.get(CORRELATION_ID), request);

            leLogger.debug("Received request: {} {}",
                    request.getMethod(), request.getRequestURI());

            final String authHeader = request.getHeader(HEADERS[0]);
            final String jwt;
            final String email;

            if (authHeader == null || !authHeader.startsWith(HEADERS[1])) {
                leLogger.debug("No JWT token found in the request. Proceeding without authentication.");
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            email = jwtService.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = findUserByEmail(email);
                UserDetails userDetails = this.userService.loadUserByUsername(user.getUsername());

                boolean isTokenValid = jwTokenRepository.findByTokenValue(jwt)
                        .map(jwToken -> !jwToken.isExpired() && !jwToken.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {

                    leLogger.info("User {} authenticated successfully.", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                filterChain.doFilter(request, response);
            }
        } catch (SignatureException | ExpiredJwtException | ResourceNotFoundException exception) {
            leLogger.error("Authentication failed: {}", exception, exception.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(ApiError.builder()
                    .path(request.getRequestURI())
                    .error(exception.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .timestamp(LocalDateTime.now())
                    .build()));

        } finally {
            MDC.remove(CORRELATION_ID);
            LELogger.clearLoggerProperties();
        }
    }

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
