package com.parunev.linkededge.util.annotations.openapi.profile;

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
@Operation(summary = "Updates user's multi-factor authentication flag", description = "Enable or disable Multi-Factor Authentication(MFA) for the user's account")
@ApiResponse(
        responseCode = "200",
        description = "MFA Status updated successfully. Response:\n" +
                "1. Your 2FA Authentication has been enabled." +
                "2. Your 2FA has been disabled."
)
@ApiResponse(
        responseCode = "404",
        description = "This response is for the case where the user is not found in the Security Context Holder",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiUpdateUserMfa {
}
