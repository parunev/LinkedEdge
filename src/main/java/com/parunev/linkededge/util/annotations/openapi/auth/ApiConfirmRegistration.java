package com.parunev.linkededge.util.annotations.openapi.auth;

import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
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
        description = "GET endpoint to validate the user's email when they verify through the email confirmation." +
                "After that simultaneously another endpoint is called which is 'https://api.lix-it.com/v1/person?profile_link={YOUR_PROFILE_ID}'," +
                "the information from linkedin profile will be scrapped and added to your profile.",
        summary = "Confirm user registration via email")
@ApiResponse(
        responseCode = "200"
        , description = "User-friendly response will be presented stating that 'Your email was confirmed successfully. You can now login.'"
        , content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RegistrationResponse.class))}
)
@ApiResponse(
        responseCode = "404"
        , description = "May occur if the provided in the request token doesn't exists in the database. Again a user-friendly response" +
        " will be presented stating the following 'Token not found. Please ensure you have the correct token or request a new one.'"
        , content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "400"
        , description = "May occur during the validation process.\n Either the token has expired, has been confirmed, or it's overall invalid.\n Possible responses are:\n" +
        "1. The provided token has already been confirmed.\n" +
        "2. The provided token has expired. Please request a new one\n" +
        "3. The user associated with this token is already enabled"
        ,content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
)
@ApiResponse(
        responseCode = "500"
        , description = "Another possible scenario in the endpoint. This may occur for few reasons.\n" +
        "1. The rate limit was exceeded - LIX API RELATED\n" +
        "2. An error occurred on the server. Should this error persist, please contact our technical team. - LIX API RELATED\n" +
        "3. The API is temporarily unavailable - LIX API RELATED\n" +
        "4. Json Extraction problems (fields might be missing if I did not cover them)\n"
        ,content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}
)
public @interface ApiConfirmRegistration {
}
