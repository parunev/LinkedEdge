package com.parunev.linkededge.util.annotations.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * The `PasswordConfirmation` annotation is used to validate that two password fields match during a user registration
 * or password update process. This annotation should be applied to the class or object containing the password fields
 * to be compared for confirmation.
 * <p>
 * When this annotation is used, it ensures that the specified password fields within the annotated class have matching
 * values. If the password fields do not match, a validation error is triggered with the default message
 * "Your passwords didn't match. Please try again."
 *
 * @author Martin Parunev
 * @date October 12, 2023
 * @see PasswordConfirmationValidator
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConfirmationValidator.class)
public @interface PasswordConfirmation {

    /**
     * Defines the error message to be used when the password fields do not match.
     *
     * @return The error message for password confirmation failure.
     */
    String message() default "Your passwords didn't match. Please try again.";

    /**
     * Groups are not used in this annotation, so this method returns an empty array.
     *
     * @return An array of validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload is not used in this annotation, so this method returns an empty array.
     *
     * @return An array of payload classes.
     */
    Class<? extends Payload>[] payload() default {};
}
