package com.elara.userservice.dto.response;

import com.elara.userservice.model.UserGroup;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupResponse {

    private String userId;

    private List<UserGroup> userGroups;

}
