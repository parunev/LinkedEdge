package com.parunev.linkededge.util.annotations.openapi.auth;

import com.parunev.linkededge.model.payload.login.ForgotPasswordResponse;
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
@Operation(
        summary = "Reset forgotten password, by receiving an email",
        description = "POST endpoint to send an email to the user with a one-time password reset url")
@ApiResponse(
        responseCode = "200",
        description = "An email has been sent to your registered email address. " +
                "The password reset link will expire in 24 hours for security reasons.",
        content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ForgotPasswordResponse.class))
        })
@ApiResponse(
        responseCode = "404",
        description = "User with the provided username not found. Please ensure you have entered the correct username",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
@ApiResponse(
        responseCode = "500",
        description = "Failed to send email",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
public @interface ApiForgotPassword {
}