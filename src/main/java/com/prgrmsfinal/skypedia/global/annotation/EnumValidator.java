package com.prgrmsfinal.skypedia.global.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class EnumValidator implements ConstraintValidator<EnumValid, Enum<?>> {
    private Set<String> allowedNames;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        allowedNames = Set.of(constraintAnnotation.includeOnly());
    }

    @Override
    public boolean isValid(Enum<?> anEnum, ConstraintValidatorContext constraintValidatorContext) {
        if (anEnum == null) {
            return true;
        }

        return allowedNames.contains(anEnum.name());
    }
}
