package com.parunev.linkededge.util.annotations.openapi.auth;

import com.parunev.linkededge.model.payload.login.LoginResponse;
import com.parunev.linkededge.security.payload.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Verify Custom or Google 2FA verification code",
        description = "POST endpoint to verify Custom or Google 2FA verification code")
@ApiResponse(
        responseCode = "200",
        description = "Friendly message stating: 'Login successful. Welcome, \" + {USERNAME} + \"!\"",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))})
@ApiResponse(
        responseCode = "404",
        description = "User with the provided username not found. Please ensure you have entered the correct username",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
@ApiResponse(responseCode = "400",
        description = "Two possible response messages here:\n" +
                "1. Multi-factor authentication is not enabled for your account.\n" +
                "2. Invalid OTP. Please ensure you have entered the correct verification code",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
public @interface ApiVerifyLogin {
}
