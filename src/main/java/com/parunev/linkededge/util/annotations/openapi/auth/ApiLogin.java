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
@Operation(
        description = "POST endpoint to log in an already existing user to the application. This endpoint returns" +
                " two JWTs. An access token for the logged-in user to use for secure endpoints and a refresh token" +
                " to ask for a freshly made access token when it expires. If the user's profile has enabled MFA, instead of" +
                "returning tokens we will first return a QR Code",
        summary = "Logs in an existing user to the app. Returns a JWT access token and a JWT refresh token or if MFA Enabled a QR Code.",
        responses = {
                @ApiResponse(
                        description = "1. Friendly message stating: 'Login successful. Welcome, \" + {USERNAME} + \"!\"\n" +
                                "2. Friendly message stating: 'Please scan the QR code with Google Authenticator app to login.'",
                        responseCode = "200",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}
                ),
                @ApiResponse(
                        description = "Unauthorized - Incorrect email or password",
                        responseCode = "401",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
                ),
                @ApiResponse(
                        description = "1. Message: Bad credentials\n" +
                                "2. Message: User with the provided username not found. Please ensure you have entered the correct username",
                        responseCode = "400"
                        ,content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
                )
        }
)
public @interface ApiLogin {
}
