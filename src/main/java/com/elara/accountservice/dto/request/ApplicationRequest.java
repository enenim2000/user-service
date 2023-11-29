package com.elara.accountservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ApplicationRequest {

    private String appName;

    private String appServer;

    private String appServerPort;
}
