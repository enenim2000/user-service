package com.elara.accountservice.dto.request;

import lombok.Data;

@Data
public class CustomerRegisterRequest {

  private String email;
  private String phone;
  private String Password;
}
