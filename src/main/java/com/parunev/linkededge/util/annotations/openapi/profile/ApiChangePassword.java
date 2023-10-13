package com.parunev.linkededge.util.annotations.openapi.profile;

import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordResponse;
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
@Operation(summary = "Change user password via the profile", description = "POST endpoint to change user's password. The user needs to be logged in order to do this.")
@ApiResponse(
        responseCode = "200"
        , description = "Changes the user's password and returns:\n" +
        "Your password has been changed! We've sent you an email!"
        , content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileChangePasswordResponse.class))}
)
@ApiResponse(
        responseCode = "400",
        description = """
                The response is for client errors. Possible descriptions for this response code include:
                1. The provided old password does not match your current one.
                2. The provided passwords does not match""",
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
public @interface ApiChangePassword {
}
