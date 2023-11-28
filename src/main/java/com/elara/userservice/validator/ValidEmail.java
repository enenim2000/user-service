package com.elara.userservice.validator;

import com.elara.userservice.validator.impl.ValidEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidEmailValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface ValidEmail {
 
    String message() default "Invalid Email";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
}