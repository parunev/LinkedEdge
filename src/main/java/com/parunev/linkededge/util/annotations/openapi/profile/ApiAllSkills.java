package com.parunev.linkededge.util.annotations.openapi.profile;

import com.parunev.linkededge.model.payload.profile.ProfileResponse;
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
@Operation(summary = "Get all user skills", description = "GET endpoint to retrieve all skill entries associated with the user's profile. The user needs to be logged in order to do this.")
@ApiResponse(
        responseCode = "200",
        description = "Skill entries retrieved successfully.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class))
)
@ApiResponse(
        responseCode = "404",
        description = "This response is for the case where the user or the profile is not found in the database or the Security Context Holder.\n" +
                "Another possible response a user can get here is 'You currently have no skill records.'",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiAllSkills {
}
