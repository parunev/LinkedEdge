package com.parunev.linkededge.security;

import com.parunev.linkededge.security.exceptions.EmailSenderException;
import com.parunev.linkededge.security.exceptions.RegistrationFailedException;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.security.payload.ConstraintError;
import com.parunev.linkededge.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    @ExceptionHandler(EmailSenderException.class)
    public ResponseEntity<ApiError> handleEmailSenderException(EmailSenderException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleEmailSenderException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiError> handleRegistrationFailedException(RegistrationFailedException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ConstraintError> handleValidationException(ConstraintViolationException ex, HttpServletRequest request) {
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
