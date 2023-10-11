package com.parunev.linkededge.model.commons;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * The `BasePayload` class is an abstract base class for payloads in the LinkedEdge application.
 * It is used to represent information related to HTTP responses, including the path, message, HTTP status, and a timestamp.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BasePayload {

    /**
     * The path associated with the payload.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    /**
     * The message contained in the payload.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    /**
     * The HTTP status code associated with the payload.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatus status;

    /**
     * The timestamp indicating when the payload was created or generated.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime timestamp;
}
