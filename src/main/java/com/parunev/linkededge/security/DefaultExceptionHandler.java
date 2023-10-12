package com.parunev.linkededge.security;

import com.parunev.linkededge.openai.exception.OpenAiException;
import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.security.payload.ConstraintError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
/**
 * The `DefaultExceptionHandler` class is responsible for handling various exceptions that might occur during API requests. It provides exception handling methods for a variety of exception types.
 * @note
 * - This class is marked with `@RestControllerAdvice` to handle exceptions globally for all REST controllers in the application.
 * </p>
 * - Each exception handling method returns an appropriate response based on the exception type.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    /**
     * Handles general exceptions and runtime exceptions by returning an `ApiError` response.
     *
     * @param e The exception to handle.
     * @param request The HTTP request that triggered the exception.
     * @return An `ApiError` response containing details of the exception.
     */
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ApiError> handleException
            (Exception e, HttpServletRequest request) {
        return new ResponseEntity<>(ApiError.builder()
                .path(request.getRequestURI())
                .error(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions related to JSON parsing and serialization errors and returns a response with the exception message.
     *
     * @param ex The JSON-related exception to handle.
     * @return A response containing the exception message and an internal server error status.
     */
    @ExceptionHandler(JSONException.class)
    public ResponseEntity<String> handleJSONException(JSONException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions related to HTTP status codes (e.g., 4xx client errors) and returns a response with the exception message and status code.
     *
     * @param ex The HTTP status code-related exception to handle.
     * @return A response containing the exception message and the specific HTTP status code.
     */
    @ExceptionHandler({HttpStatusCodeException.class, HttpClientErrorException.class})
    public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException ex){
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    /**
     * Handles exceptions related to HTTP status codes (e.g., 5xx server errors) and returns a response with the exception message and status code.
     *
     * @param ex The HTTP server error-related exception to handle.
     * @return A response containing the exception message and the specific HTTP status code.
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(HttpServerErrorException ex){
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    /**
     * Handles JWT validation exceptions and returns a response with the exception message and a status code indicating unauthorized access.
     *
     * @param ex The JWT validation exception to handle.
     * @return A response containing the exception message and an unauthorized status code.
     */
    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<String> handleJwtValidationException(JwtValidationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles exceptions related to authorization not found and returns an `ApiError` response.
     *
     * @param ex The authorization not found exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(AuthorizationNotFoundException.class)
    public ResponseEntity<ApiError> handleAuthorizationNotFoundException(AuthorizationNotFoundException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    /**
     * Handles exceptions related to resource access and returns a response with the exception message and a bad request status code.
     *
     * @param ex The resource access exception to handle.
     * @return A response containing the exception message and a bad request status code.
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to reading JSON nodes and returns an `ApiError` response.
     *
     * @param ex The JSON node reading exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(ReadingJsonNodeException.class)
    public ResponseEntity<ApiError> handleReadingJsonNodeException(ReadingJsonNodeException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to invalid extraction and returns an `ApiError` response.
     *
     * @param ex The invalid extraction exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(InvalidExtractException.class)
    public ResponseEntity<ApiError> handleInvalidExtractException(InvalidExtractException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to email sending and returns an `ApiError` response.
     *
     * @param ex The email sender exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(EmailSenderException.class)
    public ResponseEntity<ApiError> handleEmailSenderException(EmailSenderException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to invalid writing and returns an `ApiError` response.
     *
     * @param ex The invalid writing exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(InvalidWritingException.class)
    public ResponseEntity<ApiError> handleInsufficientCapacityException(InvalidWritingException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to OpenAI and returns an `ApiError` response.
     *
     * @param ex The OpenAI exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity<ApiError> handleInsufficientCapacityException(OpenAiException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to user profiles and returns an `ApiError` response.
     *
     * @param ex The user profile exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(UserProfileException.class)
    public ResponseEntity<ApiError> handleUserProfileException(UserProfileException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to OTP (One-Time Password) validation and returns an `ApiError` response.
     *
     * @param ex The OTP validation exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(OTPValidationException.class)
    public ResponseEntity<ApiError> handleOTPValidationException(OTPValidationException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    /**
     * Handles exceptions related to resource not found and returns an `ApiError` response.
     *
     * @param ex The resource not found exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    /**
     * Handles exceptions related to authentication service errors and returns an `ApiError` response.
     *
     * @param ex The authentication service exception to handle.
     * @return An `ApiError` response containing details of the error and its status.
     */
    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ApiError> handleAuthServiceException(AuthServiceException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    /**
     * Handles validation exceptions related to constraint violations and returns a `ConstraintError` response.
     *
     * @param ex The constraint violation exception to handle.
     * @return A `ConstraintError` response containing details of the validation errors and a bad request status code.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ConstraintError> handleValidationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(error -> {
            String errorMessage = error.getMessage();
            errors.put("VIOLATED CONSTRAINT", errorMessage);
        });

        return new ResponseEntity<>(ConstraintError.builder()
                .path(getCurrentRequest())
                .message("Validation failed. One or more constraints were violated.")
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }
}
