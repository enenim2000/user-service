package com.elara.accountservice.dto.response;

import com.elara.accountservice.domain.UserGroup;
import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupResponse extends BaseResponse {

    private String userId;

    private List<UserGroup> userGroups;

}
