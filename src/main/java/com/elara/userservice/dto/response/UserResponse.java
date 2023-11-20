package com.elara.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;

    private String companyCode;

    private String email;

    private String phone;

    private boolean isEmailVerified;

    private boolean isPhoneVerified;

    private String status;

    private String createdAt;

    private String updatedAt;
}
