package com.parunev.linkededge.util.annotations.nobadwords;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BadWordsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoBadWords {
    String message() default "The provided question contains inappropriate language.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
