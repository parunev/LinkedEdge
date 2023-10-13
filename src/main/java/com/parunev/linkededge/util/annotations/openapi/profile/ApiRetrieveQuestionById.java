package com.parunev.linkededge.util.annotations.openapi.profile;

import com.parunev.linkededge.model.payload.interview.QuestionResponse;
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
        summary = "Get Question by ID",
        description = "GET endpoint to retrieve a question by its unique identifier."
)
@ApiResponse(
        responseCode = "200",
        description = "Question retrieved successfully.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))
)
@ApiResponse(
        responseCode = "400",
        description = "The specified question does not belong to the user's profile or another client error.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
)
@ApiResponse(
        responseCode = "404",
        description = "Question not found in the database.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
)
public @interface ApiRetrieveQuestionById {
}
