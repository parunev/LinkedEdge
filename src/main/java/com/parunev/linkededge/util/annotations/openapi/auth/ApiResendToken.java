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
        description = "POST endpoint to request a new confirmation token which is needed for the account enabling.",
        summary = "Request for a new confirmation token")
@ApiResponse(
        responseCode = "200"
        , description = "User-friendly response will be presented stating that 'A new confirmation email has been sent to your email address.'"
        , content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RegistrationResponse.class))}
)
@ApiResponse(
        responseCode = "400"
        , description = "May occur during the validation process. Possible responses are: \n" +
        "1. Your account is already enabled. You can now log in.\n" +
        "2. The provided token has expired. Please request a new one\n" +
        "3. The user associated with this token is already enabled\n" +
        "Quite possible is to receive a 'Failed to send email.' if there is a problem with the email service"
        ,content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiResendToken {
}
