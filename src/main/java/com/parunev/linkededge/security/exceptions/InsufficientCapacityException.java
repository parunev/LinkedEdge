package com.parunev.linkededge.security.exceptions;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InsufficientCapacityException extends RuntimeException{

    private final transient ApiError apiError;

    public InsufficientCapacityException(ApiError message) {
        this.apiError = message;
    }
}
