package com.elara.userservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class NotificationRequest {

    private String companyCode;
    private String appId;
    private String userId;
    private String to;
    private String message;
    private String html;

}
