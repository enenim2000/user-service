package com.elara.userservice.dto.response;

import com.elara.userservice.auth.AuthToken;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse extends BaseResponse {

    private AuthToken data;
}
