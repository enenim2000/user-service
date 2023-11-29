package com.elara.accountservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String companyCode;

    private String email;

    private String phone;

}
