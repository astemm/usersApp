package com.koblan.usersapp.controller.annotation;

import com.koblan.usersapp.controller.UsersController.GetTaskRequestParameters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ToIsMoreRecentThanFromValidator implements ConstraintValidator<ToIsMoreRecentThanFrom, GetTaskRequestParameters> {

    @Override
    public boolean isValid(GetTaskRequestParameters value,
                           ConstraintValidatorContext context) {
        if (value.from().isAfter(value.to())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("From (%s) is after to (%s), which is incorrect.", value.from(), value.to()))
                   .addConstraintViolation();
            return false;
        }
        return true;
    }

}
