package com.elara.accountservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyRequest {

    private String companyName;

    private String companyAddress;

}
