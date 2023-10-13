package com.parunev.linkededge.util.annotations.openapi.profile;

import com.parunev.linkededge.model.payload.interview.AnswerResponse;
import com.parunev.linkededge.security.payload.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get All User Answers", description = "GET endpoint to retrieve a list of answers with optional filtering related to the user. The user needs to be logged in order to do this.")
@ApiResponse(
        responseCode = "200",
        description = "Questions retrieved successfully.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class), additionalPropertiesSchema = @Schema(implementation = AnswerResponse.class))
)
@ApiResponse(
        responseCode = "404",
        description = "This response is for the case where the user or the profile is not found in the database or the Security Context Holder.\n" +
                "Another possible response a user can get here is 'You currently have no experience records.'",
        content = {@Content(mediaType="application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiAllAnswers {
}
