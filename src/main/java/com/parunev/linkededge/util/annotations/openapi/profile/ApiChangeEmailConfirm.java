package com.parunev.linkededge.util.annotations.openapi.profile;

import com.parunev.linkededge.model.payload.profile.email.ProfileEmailResponse;
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
@Operation(summary = "Confirms the change of the user's email", description = "Confirm the change of the user's email by providing a token and new email.")
@ApiResponse(
        responseCode = "200",
        description = "Email change is confirmed successfully.\n" +
                "Response: Your email was been changed successfully.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileEmailResponse.class))
)
@ApiResponse(
        responseCode = "400",
        description = """
                The response is for client errors. Possible descriptions for this response code include:
                1. The provided token has already been confirmed
                2. The provided token has expired. Please request a new one""",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "404",
        description = "Token not found. Please ensure you have the correct token or request a new one.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
)
public @interface ApiChangeEmailConfirm {
}
