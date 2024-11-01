package com.example.spring_study.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SortFieldValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateSortField {
    String message() default "Invalid sort field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // New parameter for the target class
    Class<?> entityClass();
}