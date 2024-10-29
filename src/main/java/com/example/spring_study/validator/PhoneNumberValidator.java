package com.example.spring_study.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final String PHONE_NUMBER_PATTERN =
            "^(0[1-9]\\d{8}|(\\+84[1-9]\\d{8}))$";

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null) {
            return true; // or false based on your validation logic
        }
        return Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }
}
