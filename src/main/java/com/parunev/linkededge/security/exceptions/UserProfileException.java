package com.parunev.linkededge.security.exceptions;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserProfileException extends RuntimeException{

    private final transient ApiError apiError;

    public UserProfileException(ApiError message) {
        this.apiError = message;
    }
}
