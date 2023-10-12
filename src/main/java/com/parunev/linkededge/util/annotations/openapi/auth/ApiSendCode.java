package com.parunev.linkededge.util.annotations.openapi.auth;

import com.parunev.linkededge.model.payload.login.VerificationResponse;
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
@Operation(summary = "Send Custom 2FA verification code to user's email",
        description = "POST endpoint to send Custom 2FA verification code to user's email")
@ApiResponse(
        responseCode = "200",
        description = "Verification code sent successfully. Please check your email for the code",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VerificationResponse.class))})
@ApiResponse(
        responseCode = "404",
        description = "User with the provided username not found. Please ensure you have entered the correct username",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
@ApiResponse(
        responseCode = "400",
        description = "Multi-factor authentication is not enabled for your account.",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
@ApiResponse(
        responseCode = "500",
        description = "Failed to send email",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
@ApiResponse(
        responseCode = "501",
        description = "Failed to fetch pair from OTP cache",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
public @interface ApiSendCode {
}
