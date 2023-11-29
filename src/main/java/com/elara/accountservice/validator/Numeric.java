package com.elara.accountservice.validator;

import com.elara.accountservice.validator.impl.NumericValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NumericValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Numeric {

    boolean required() default true;

    public String message() default "Invalid numeric value";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}