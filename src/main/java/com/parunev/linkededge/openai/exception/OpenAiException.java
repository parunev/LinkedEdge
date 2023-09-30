package com.parunev.linkededge.openai.exception;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OpenAiException extends RuntimeException{
    private final transient ApiError apiError;
}
