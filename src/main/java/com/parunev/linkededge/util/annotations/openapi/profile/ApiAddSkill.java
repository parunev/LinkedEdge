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
@Operation(summary = "Adds user skill to the profile", description = "POST endpoint to add new skill entry to the user's profile. The user needs to be logged in order to do this.")
@ApiResponse(
        responseCode = "201",
        description = """
                Skill entry added successfully. Response:
                You've successfully added a new skill to your profile:
                Skill name: %s
                Skill level: %d""",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class))
)
@ApiResponse(
        responseCode = "400",
        description = """
                The response is for client errors. Possible descriptions for this response code include:
                1. The provided skill is not valid. (This validation might not work as expected as always since it's done via Gpt 3.5 Turbo model
                2. Sorry, not enough extra capacity for experiences. Consider buying more credits for your profile!
                3. The skill you're trying to enter is already in your account""",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "404",
        description = "This response is for the case where the user or the profile is not found in the database or the Security Context Holder",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiAddSkill {
}
