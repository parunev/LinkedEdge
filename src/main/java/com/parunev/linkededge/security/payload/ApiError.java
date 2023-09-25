package com.parunev.linkededge.security.payload;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ApiError {

    private String path;
    private String error;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
