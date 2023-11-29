package com.elara.accountservice.dto.response;

import com.elara.accountservice.auth.AuthToken;
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
