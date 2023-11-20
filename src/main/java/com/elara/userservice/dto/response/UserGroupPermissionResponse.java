package com.elara.userservice.dto.response;

import com.elara.userservice.model.ApplicationPermission;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupPermissionResponse {

    private long groupId;

    private List<ApplicationPermission> permissions;

}
