package com.parunev.linkededge.openai.exception;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: Custom exception class for OpenAI-related errors.
 * <p>
 * This class represents a custom runtime exception tailored to handle errors and exceptions related to OpenAI operations.
 * It includes an `ApiError` object to provide detailed error information when exceptions are thrown.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Getter
@RequiredArgsConstructor // Constructs an `OpenAiException` with the specified `ApiError`.
public class OpenAiException extends RuntimeException{
    /**
     * The `ApiError` object containing detailed error information.
     */
    private final transient ApiError apiError;
}
