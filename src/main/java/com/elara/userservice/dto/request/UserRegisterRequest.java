package com.elara.userservice.dto.request;

import com.elara.userservice.validator.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @Required(message = "${password.required}")
    private String password;

    @Required(message = "${email.required}")
    private String email;

    @Required(message = "${phone.required}")
    private String phone;

}
