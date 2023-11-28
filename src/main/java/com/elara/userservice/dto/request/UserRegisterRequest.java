package com.elara.userservice.dto.request;

import com.elara.userservice.validator.Required;
import com.elara.userservice.validator.ValidEmail;
import com.elara.userservice.validator.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @Required(message = "password.required")
    @ValidPassword(message = "password.valid")
    private String password;

    @Required(message = "email.required")
    @ValidEmail(message = "email.valid")
    private String email;

    @Required(message = "phone.required")
    private String phone;

}
