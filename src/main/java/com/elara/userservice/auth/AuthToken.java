package com.elara.userservice.auth;

import java.util.List;
import lombok.Data;

@Data
public class AuthToken {

  private String companyCode;

  private String companyName;

  private String uuid;

  private String email;

  private String phone;

  private String firstName;

  private String lastName;

  private String middleName;

  private String username;

  private String lang;

  private String accessToken;

  private String refreshToken;

  List<String> audience;

  private String expires;
}
