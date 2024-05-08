package com.koblan.usersapp.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ToIsMoreRecentThanFromValidator.class)
public @interface ToIsMoreRecentThanFrom {
    String message() default "'to' should be more recent then 'from'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
