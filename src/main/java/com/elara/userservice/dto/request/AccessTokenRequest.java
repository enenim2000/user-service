package com.elara.userservice.dto.request;

import com.elara.userservice.dto.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenRequest extends BaseResponse {

    private String refreshToken;
}
