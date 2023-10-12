package com.parunev.linkededge.util.annotations.openapi.auth;

import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
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
        description = "POST Endpoint to sign up to the application. After successful completion, the server" +
                " sends an email to the user in order for them to confirm their account and complete the sign up process.",
        summary = "Registration process where the user creates his account.",
        responses = {
                @ApiResponse(
                        description = "User-Friendly message should be the response:" +
                                " 'Your registration was completed successfully. Please confirm your email account.'",
                        responseCode = "200",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RegistrationResponse.class))}
                ),
                @ApiResponse(
                        description = "One of the following user-friendly messages should be response:\n" +
                                "1. Email and username already exist. Please try different ones.\n" +
                                "2. Email already exists. Please try another one.\n" +
                                "3. Username already exists. Please try another one.\n" +
                                "Quite possible is to receive a 'Failed to send email.' if there is a problem with the email service",
                        responseCode = "400",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
                )
        }
)
public @interface ApiRegisterUser {

}
