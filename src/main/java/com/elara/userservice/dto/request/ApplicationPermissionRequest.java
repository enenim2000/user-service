package com.elara.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationPermissionRequest {

    private String permissionId;

    private String permission;

    private String description;

    private String uriPath;

    private boolean isSecured;
}
