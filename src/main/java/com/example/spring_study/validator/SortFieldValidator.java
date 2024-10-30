package com.example.spring_study.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortFieldValidator implements ConstraintValidator<ValidateSortField, String> {
    private Set<String> allowedFields;

    @Override
    public void initialize(ValidateSortField constraintAnnotation) {
        Class<?> targetClass = constraintAnnotation.targetClass();

        // Use reflection to get all field names of the target class
        allowedFields = Stream.of(targetClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Return true if value is null (optional field) or if it's in the allowed fields
        return value == null || allowedFields.contains(value);
    }
}
