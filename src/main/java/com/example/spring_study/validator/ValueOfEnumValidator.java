package com.example.spring_study.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValueOfEnum annotation) {
        enumClass = annotation.enumClass();
        acceptedValues = Stream.of(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (!acceptedValues.contains(value.toString())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("Invalid value: '%s'. Accepted values for %s are: %s",
                                    value,
                                    enumClass.getSimpleName(),
                                    String.join(", ", acceptedValues)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
