package com.parunev.linkededge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parunev.linkededge.security.jwt.JwtFilter;
import com.parunev.linkededge.security.jwt.JwtLogout;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.security.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

/**
 * @Description: Configuration class for defining security settings and filters in the application.
 * <p>
 * This class configures security-related settings, such as JWT authentication and exception handling,
 * and defines filters to protect the application's endpoints. It enables web security and method-level security.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final JwtLogout jwtLogout;
    private final ObjectMapper objectMapper;

    /**
     * Bean definition for the security filter chain, which defines security configurations and filters.
     *
     * @param http The HTTP security configuration builder.
     * @return SecurityFilterChain for protecting the application's endpoints.
     * @throws Exception If there are any security configuration exceptions.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disables Cross-Site Request Forgery (CSRF) protection.
                .httpBasic(AbstractHttpConfigurer::disable) // Disables HTTP Basic authentication.
                .anonymous(AbstractHttpConfigurer::disable) // Disables anonymous user access.
                .formLogin(AbstractHttpConfigurer::disable) // Disables form-based login.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) ->{
                            // Handles unauthorized access and authentication failures.
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiError.builder()
                                            .path(request.getRequestURI())
                                            .error(authException.getMessage())
                                            .timestamp(LocalDateTime.now())
                                            .status(HttpStatus.UNAUTHORIZED)
                                            .build()
                            ));
                        })))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/edge-api/v1/auth/**").permitAll() // Permits public access to specific endpoints.
                        .requestMatchers("/edge-api/v1/profile/**").permitAll()
                        .requestMatchers("/v2/api-docs", "/v3/api-docs",
                                "/v3/api-docs/**", "/swagger-resources",
                                "/swagger-resources/**", "/configuration/ui",
                                "/configuration/security", "/swagger-ui/**",
                                "/webjars/**", "swagger-ui.html").permitAll()
                        .anyRequest().authenticated()) // Requires authentication for any other requests.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Integrates JWT authentication.
                .logout(logout -> logout
                        .logoutUrl("/edge-api/v1/auth/logout") // Specifies the logout URL.
                        .addLogoutHandler(jwtLogout) // Adds custom logout handling.
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            // Handles user logout and clears the security context.
                            response.setContentType("application/json");
                            final String authHeader = request.getHeader("Authorization");

                            String message;
                            HttpStatus status;
                            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                                message = "There is no authenticated user.";
                                status = HttpStatus.UNAUTHORIZED;
                            } else {
                                message = "User successfully logged out.";
                                status = HttpStatus.OK;
                            }

                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.builder()
                                            .path(request.getRequestURI())
                                            .message(message)
                                            .timestamp(LocalDateTime.now())
                                            .status(status)
                                            .build()
                            ));
                            SecurityContextHolder.clearContext(); // Clears the security context.
                        })))
                .build();
    }
}
