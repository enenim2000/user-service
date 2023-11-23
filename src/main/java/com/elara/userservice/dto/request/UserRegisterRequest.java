package com.elara.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    private String password;

    private String email;

    private String phone;

}
