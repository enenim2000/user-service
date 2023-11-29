package com.elara.userservice.dto.response;

import com.elara.userservice.domain.ApplicationPermission;
import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupPermissionResponse extends BaseResponse{

    private long groupId;

    private List<ApplicationPermission> permissions;

}
