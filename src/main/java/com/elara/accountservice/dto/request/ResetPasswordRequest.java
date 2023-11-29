package com.elara.accountservice.dto.request;

import com.elara.accountservice.validator.Required;
import com.elara.accountservice.validator.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    private String otp;

    @Required(message = "password.required")
    @ValidPassword(message = "password.valid")
    private String newPassword;

    private String confirmPassword;

}
