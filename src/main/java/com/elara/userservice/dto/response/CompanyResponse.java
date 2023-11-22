package com.elara.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyResponse extends BaseResponse {

    private Long id;

    private String companyName;

    private String companyCode;

    private String companyAddress;

    private String clientId;

    private String clientSecret;

    private String status;

    private String createdAt;

    private String updatedAt;
}
