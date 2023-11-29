package com.elara.accountservice.dto.request;

import com.elara.accountservice.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationVerifyRequest {
    private String otp;
    private NotificationType notificationType;
}

