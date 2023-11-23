package com.elara.userservice.validator.impl;

import com.elara.userservice.enums.ResponseCode;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.service.MessageService;
import com.elara.userservice.validator.Numeric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

@Slf4j
@Component
public class NumericValidator implements ConstraintValidator<Numeric, Object> {

    boolean required;
    String message;

    @Autowired
    MessageService messageService;

    @Override
    public void initialize(Numeric annotation) {
        message = annotation.message();
        required = annotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cvc) {
        if (!required && value == null) {
            return true;
        }

        String castValue = "";
        if (value instanceof BigInteger) {
            castValue = value.toString();
        } else if (value instanceof Long) {
            castValue = value + "";
        } else if (value instanceof Integer) {
            castValue = value + "";
        } else if (value instanceof String) {
            castValue = (String) value;
        } else {
            castValue = value + "";
        }

        if (!required && castValue.trim().isEmpty()) {
            return true;
        } else if (required && castValue.matches("\\d+")) {
            return true;
        }

        throw new AppException(messageService.getMessage(this.message));
    }
}
