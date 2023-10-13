package com.parunev.linkededge.util.annotations.openapi.interview;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Generate Answer for User Question", description = "Generates an answer for a user's specialized interview question using OpenAI's natural language processing.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful request, returns an answer to the user's question"),
        @ApiResponse(responseCode = "400", description = "If an error occurs during the request or answer generation"),
        @ApiResponse(responseCode = "401", description = "User is not authorized to access this endpoint")
})
public @interface ApiAnswerSpecificQuestion {
}
