package com.elara.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAccountRequest {

    private String companyCode;

    private String userId;

    private String permissionId;

}
