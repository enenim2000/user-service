package com.elara.userservice.validator;

import com.elara.userservice.validator.impl.DecimalValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DecimalValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Decimal {

    boolean required() default true;

    public String message() default "Invalid decimal value";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}