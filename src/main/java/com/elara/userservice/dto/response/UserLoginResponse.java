package com.elara.userservice.dto.response;

import com.elara.userservice.auth.AuthToken;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginResponse extends BaseResponse {

    private AuthToken data;
}
