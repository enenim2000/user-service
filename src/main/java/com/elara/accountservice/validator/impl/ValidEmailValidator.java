package com.elara.accountservice.validator.impl;

import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.service.MessageService;
import com.elara.accountservice.validator.ValidEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Autowired
    MessageService messageService;

    String message;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public void initialize(ValidEmail annotation) {
        message = annotation.message();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cvc) {
        boolean valid = email == null || validate(email);
        if (valid) {
            return true;
        }

        throw new AppException(messageService.getMessage(this.message));
    }

    private static boolean validate(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }
}
