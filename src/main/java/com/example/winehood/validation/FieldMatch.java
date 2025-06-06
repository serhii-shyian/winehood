package com.example.winehood.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMatch {
    String message() default "Fields values don't match!";
    String field();
    String fieldMatch();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
