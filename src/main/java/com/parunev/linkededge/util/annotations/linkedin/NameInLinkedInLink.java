package com.parunev.linkededge.util.annotations.linkedin;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameInLinkedInLinkValidator.class)
public @interface NameInLinkedInLink {
    String message() default "The first and last names do not match the names in the LinkedIn profile link.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
