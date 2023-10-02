package com.parunev.linkededge.security.exceptions;

import com.parunev.linkededge.security.payload.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EducationNotFoundException extends RuntimeException{

    private final transient ApiError error;

    public EducationNotFoundException(ApiError message) {
        this.error = message;
    }
}
