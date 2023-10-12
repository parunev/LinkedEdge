package com.parunev.linkededge.util.annotations.nobadwords;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * A custom validation annotation to check if a field's value does not contain inappropriate language.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Documented
@Constraint(validatedBy = BadWordsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoBadWords {
    /**
     * Defines the default error message to be used if the validation fails.
     *
     * @return The default error message.
     */
    String message() default "The provided question contains inappropriate language.";

    /**
     * Groups to which this constraint belongs. The default is an empty array.
     *
     * @return An array of classes that represent validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload type associated with this constraint. The default is an empty array.
     *
     * @return An array of payload classes.
     */
    Class<? extends Payload>[] payload() default {};
}
