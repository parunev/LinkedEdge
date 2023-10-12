package com.parunev.linkededge.openai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: Enumeration of error codes and messages for OpenAI API.
 * <p>
 * This enum defines error codes and associated error messages for handling various error scenarios when interacting with the OpenAI API.
 * Each enum constant represents a specific error, providing both a code and a descriptive message to assist in error identification and handling.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Getter
@RequiredArgsConstructor
public enum OpenAiError {

    /**
     * Invalid authentication error.
     */
    INVALID_AUTHENTICATION(401, "Invalid Authentication"),

    /**
     * Incorrect API key provided error.
     */
    INCORRECT_API_KEY_PROVIDED(401, "Incorrect API key provided"),

    /**
     * Membership requirement error.
     */
    MUST_BE_A_MEMBER(401, "You must be a member of an organization to use the API"),

    /**
     * Rate limit exceeded error.
     */
    RATE_LIMIT_REACHED(429, "Rate limit reached for requests"),

    /**
     * Quota exceeded error.
     */
    EXCEEDED_YOUR_CURRENT_QUOTA(429, "You exceeded your current quota, please check your plan and billing details"),

    /**
     * Engine overload error.
     */
    ENGINE_IS_CURRENTLY_OVERLOADED(429, "The engine is currently overloaded, please try again later"),

    /**
     * Server error.
     */
    SERVER_HAD_AN_ERROR(500, "The server had an error while processing your request");

    /**
     * The error code.
     */
    private final Integer code;

    /**
     * The descriptive error message.
     */
    private final String msg;
}
