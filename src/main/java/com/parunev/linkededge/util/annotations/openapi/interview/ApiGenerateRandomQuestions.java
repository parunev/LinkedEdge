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
@Operation(summary = "Generate Random Interview Questions", description = "Generates a list of random interview questions based on user preferences.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful request, returns a list of randomly generated interview questions"),
        @ApiResponse(responseCode = "400", description = "If an error occurs during the request or question generation"),
        @ApiResponse(responseCode = "401", description = "User is not authorized to access this endpoint")
})
public @interface ApiGenerateRandomQuestions {
}
