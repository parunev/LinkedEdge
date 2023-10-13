package com.parunev.linkededge.security.payload;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * The `ConstraintError` class is a specialized payload used to represent constraint validation errors in the application. It extends the `BasePayload` class, which contains common attributes.
 * <p>
 * Example Usage:
 * <p>
 * ConstraintError error = ConstraintError.builder()
 * <p>
 *                                   .errors(Map.of(
 *                                   <p>
 *                                       "username", "Username must be unique.",
 *                                       <p>
 *                                       "email", "Email format is invalid."
 *                                       <p>
 *                                   ))
 *                                   <p>
 *                                   .build();
 * ```
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ConstraintError extends BasePayload {

    /**
     * A map that associates field names with corresponding error messages. It provides detailed information about which fields failed validation and the specific error messages for each field.
     */
    private Map<String, String> errors;
}
