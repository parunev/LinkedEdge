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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final JwtLogout jwtLogout;
    private final ObjectMapper objectMapper;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) ->{
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
                        .requestMatchers("/edge-api/v1/auth/**").permitAll()
                        .requestMatchers("/edge-api/v1/interview/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/edge-api/v1/auth/logout")
                        .addLogoutHandler(jwtLogout)
                        .logoutSuccessHandler(((request, response, authentication) -> {
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
                            SecurityContextHolder.clearContext();
                        })))
                .build();
    }
}
