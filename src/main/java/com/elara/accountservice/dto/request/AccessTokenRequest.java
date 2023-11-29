package com.elara.accountservice.dto.request;

import com.elara.accountservice.dto.response.BaseResponse;
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
