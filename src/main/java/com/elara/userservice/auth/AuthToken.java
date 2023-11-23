package com.elara.userservice.auth;

import lombok.Data;

import java.util.List;

@Data
public class AuthToken {

  private String companyCode;

  private String companyName;

  private String email;

  private String phone;

  private String username;

  private String lang;

  private String token;

  private String refreshToken;

  List<String> audience;

  private String expires;
}
