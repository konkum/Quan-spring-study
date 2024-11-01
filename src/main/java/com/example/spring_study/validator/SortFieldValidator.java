package com.example.spring_study.validator;

import jakarta.persistence.Embedded;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortFieldValidator implements ConstraintValidator<ValidateSortField, String> {
    private static class FieldInfo {
        final Class<?> clazz;
        final String prefix;

        FieldInfo(Class<?> clazz, String prefix) {
            this.clazz = clazz;
            this.prefix = prefix;
        }
    }

    private Set<String> allowedFields;

    @Override
    public void initialize(ValidateSortField constraintAnnotation) {
        allowedFields = new HashSet<>();
        Class<?> entityClass = constraintAnnotation.entityClass();
        Set<Class<?>> processedClasses = new HashSet<>();
        processClass(entityClass, processedClasses);
    }

    private void processClass(Class<?> rootClass, Set<Class<?>> processedClasses) {
        Queue<FieldInfo> queue = new LinkedList<>();
        queue.add(new FieldInfo(rootClass, ""));

        while (!queue.isEmpty()) {
            FieldInfo fieldInfo = queue.poll();
            Class<?> currentClass = fieldInfo.clazz;
            String prefix = fieldInfo.prefix;

            if (!processedClasses.add(currentClass)) {
                continue;
            }

            for (Field field : currentClass.getDeclaredFields()) {
                String fieldName = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
                allowedFields.add(fieldName);

                Class<?> fieldType = field.getType();
                if (shouldProcessField(field, fieldType) && !processedClasses.contains(fieldType)) {
                    queue.add(new FieldInfo(fieldType, fieldName));
                }
            }

            Class<?> superclass = currentClass.getSuperclass();
            if (superclass != null && superclass != Object.class && !processedClasses.contains(superclass)) {
                queue.add(new FieldInfo(superclass, prefix));
            }
        }
    }

    private boolean shouldProcessField(Field field, Class<?> fieldType) {
        return field.isAnnotationPresent(Embedded.class) ||
                (!fieldType.isPrimitive() &&
                        !fieldType.getName().startsWith("java.lang.") &&
                        !fieldType.getName().startsWith("java.time.") &&
                        !fieldType.getName().startsWith("java.util.") &&
                        !fieldType.isEnum() &&
                        !fieldType.isArray());
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String trimmedValue = value.trim();

        // Handle sort direction suffix if present
        String fieldName = trimmedValue;
        if (trimmedValue.endsWith(".asc") || trimmedValue.endsWith(".desc")) {
            fieldName = trimmedValue.substring(0, trimmedValue.lastIndexOf('.'));
        }

        if (!allowedFields.contains(fieldName)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid sort field: '" + trimmedValue +
                            "'. Valid fields are: " + allowedFields.stream().sorted().collect(Collectors.joining(", ")))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
