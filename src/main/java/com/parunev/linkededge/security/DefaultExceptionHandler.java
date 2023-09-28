package com.parunev.linkededge.security;

import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.security.payload.ConstraintError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ApiError> handleException
            (Exception e, HttpServletRequest request) {
        return new ResponseEntity<>(ApiError.builder()
                .path(request.getRequestURI())
                .error(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JSONException.class)
    public ResponseEntity<String> handleJSONException(JSONException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({HttpStatusCodeException.class, HttpClientErrorException.class})
    public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException ex){
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler(ReadingJsonNodeException.class)
    public ResponseEntity<ApiError> handleReadingJsonNodeException(ReadingJsonNodeException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiError> handleProfileNotFoundException(ProfileNotFoundException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    @ExceptionHandler(InvalidExtractException.class)
    public ResponseEntity<ApiError> handleInvalidExtractException(InvalidExtractException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(EmailSenderException.class)
    public ResponseEntity<ApiError> handleEmailSenderException(EmailSenderException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    @ExceptionHandler(UserAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleUserAlreadyEnabledException(UserAlreadyEnabledException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    @ExceptionHandler(UserMfaNotEnabledException.class)
    public ResponseEntity<ApiError> handleUserMfaNotEnabledException(UserMfaNotEnabledException ex){
        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiError> handleRegistrationFailedException(RegistrationFailedException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(OTPValidationException.class)
    public ResponseEntity<ApiError> handleOTPValidationException(OTPValidationException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(InvalidPasswordResetException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordResetException(InvalidPasswordResetException ex){
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ConstraintError> handleValidationException(ConstraintViolationException ex) {
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
