package com.example.spring_study.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class StringListValidator implements ConstraintValidator<ValidateRole, Set<String>> {
    private final Set<String> allowedValues = new HashSet<>(Set.of("ROLE_ADMIN", "ROLE_USER"));

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean hasAllowedValue = value.stream().anyMatch(allowedValues::contains);

        boolean hasInvalidValue = value.stream().anyMatch(v -> !allowedValues.contains(v));

        return hasAllowedValue && !hasInvalidValue;
    }
}
