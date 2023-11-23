package com.elara.userservice.validator;

import com.elara.userservice.validator.impl.RequiredValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Required {

    public String message() default "Field is required";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
