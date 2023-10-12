package com.parunev.linkededge.util.annotations.linkedin;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * A custom validation annotation used to validate that the first and last names in a user's LinkedIn profile link
 * match the names provided in a form or data object. This annotation is used at the class level.
 * <p>
 * When applied to a class, it indicates that the validation logic provided by the {@link NameInLinkedInLinkValidator}
 * class should be executed to ensure that the first and last names match the names in the LinkedIn profile link.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameInLinkedInLinkValidator.class)
public @interface NameInLinkedInLink {

    /**
     * The message to be shown when validation fails.
     *
     * @return The error message to display when validation fails.
     */
    String message() default "The first and last names do not match the names in the LinkedIn profile link.";

    /**
     * Groups for validation. Not used in this annotation.
     *
     * @return An array of validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload for the annotation. Not used in this annotation.
     *
     * @return An array of payload classes.
     */
    Class<? extends Payload>[] payload() default {};
}
