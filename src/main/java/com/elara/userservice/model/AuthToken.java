package com.elara.userservice.model;

import com.elara.userservice.dto.response.ApplicationPermissionResponse;
import java.util.List;
import javax.persistence.Column;
import lombok.Data;

@Data
public class AuthToken {

  private String companyCode;

  private String email;

  private String phone;

  private String username;

  private String lang;

  List<ApplicationPermissionResponse> permissions;
  List<UserGroup> groups;
}
