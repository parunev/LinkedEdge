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
@Operation(summary = "Change user email via the profile", description = "POST endpoint to change user's email. The user needs to be logged in order to use this.")
@ApiResponse(
        responseCode = "200",
        description = "Changes the user's email and returns:\n" +
                "Email change request sent. Please confirm your new email address!",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ProfileEmailResponse.class))}
)
@ApiResponse(
        responseCode = "400",
        description = """
                The response is for client errors. Possible descriptions for this response code include:
                1. The provided email is the same as your current one.
                2. The provided email is already associated with another user profile
                3. The provided password does not match your password.""",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "404",
        description = "This response is for the case where the user is not found in the Security Context Holder",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "500",
        description = "This response is for server errors related to sending the email",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiChangeEmail {
}
