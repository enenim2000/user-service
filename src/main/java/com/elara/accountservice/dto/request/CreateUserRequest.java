package com.elara.accountservice.dto.request;

import com.elara.accountservice.validator.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @Required(message = "companyCode.required")
    private String companyCode;

    @Required(message = "email.required")
    private String email;

    @Required(message = "phone.required")
    private String phone;

    @Required(message = "lang.required")
    private String lang;
}
