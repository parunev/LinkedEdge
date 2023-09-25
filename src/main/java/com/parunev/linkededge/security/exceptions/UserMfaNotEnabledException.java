package com.parunev.linkededge.security.exceptions;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserMfaNotEnabledException extends RuntimeException{

    private final transient ApiError error;

    public UserMfaNotEnabledException(ApiError message) {
        this.error = message;
    }
}
