package com.elara.accountservice.validator.impl;

import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.service.MessageService;
import com.elara.accountservice.validator.Decimal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class DecimalValidator implements ConstraintValidator<Decimal, Object> {

    boolean required;
    String message;

    @Autowired
    MessageService messageService;

    @Override
    public void initialize(Decimal annotation) {
        message = annotation.message();
        required = annotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cvc) {
        if (!required && value == null) {
            return true;
        }

        String castValue = "";
        if (value instanceof BigDecimal) {
            castValue = value.toString();
        } else if (value instanceof Double) {
            castValue = value.toString();
        } else if (value instanceof Integer) {
            castValue = value + "";
        } else if (value instanceof String) {
            castValue = (String) value;
        } else {
            castValue = value + "";
        }

        if (castValue.matches("^-?(?!.{12})\\d+(?:\\.\\d+)?$")) {
            return true;
        }

        throw new AppException(messageService.getMessage(this.message));
    }
}