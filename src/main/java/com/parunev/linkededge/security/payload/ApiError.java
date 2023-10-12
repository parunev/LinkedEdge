package com.parunev.linkededge.security.payload;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
/**
 * The `ApiError` class represents a structured error response in the application. It provides a standardized format for conveying error details to clients or developers.
 * <p>
 * Example Usage:
 * <p>
 * ApiError error = ApiError.builder()
 * <p>
 *                         .path("/api/users/123")
 *                         <p>
 *                         .error("User not found")
 *                         <p>
 *                         .status(HttpStatus.NOT_FOUND)
 *                         <p>
 *                         .timestamp(LocalDateTime.now())
 *                         <p>
 *                         .build();
 * ```
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ApiError {

    /**
     * The URI or endpoint where the error occurred, providing context about the request that led to the error.
     */
    private String path;

    /**
     * A human-readable error message or description explaining the nature of the error.
     */
    private String error;

    /**
     * The HTTP status code associated with the error, indicating the response status, such as 404 (Not Found) or 500 (Internal Server Error).
     */
    private HttpStatus status;

    /**
     * The date and time when the error occurred, aiding in tracking and debugging issues.
     */
    private LocalDateTime timestamp;
}
