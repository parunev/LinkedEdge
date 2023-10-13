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
@Operation(summary = "Prepare for a Job", description = "Generates job preparations based on user's current information and job link from the request.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful request, returns job preparations"),
        @ApiResponse(responseCode = "400", description = "If an error occurs during the request"),
        @ApiResponse(responseCode = "401", description = "User is not authorized to access this endpoint")
})
public @interface ApiPrepareMeForJob {
}
