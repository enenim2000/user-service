package com.elara.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {

    private long id;

    private String appName;

    private String appServer;

    private String appServerPort;

    private String status;

    private String createdAt;

    private String updatedAt;
}
