package com.elara.accountservice.dto.request;

import com.elara.accountservice.enums.NotificationType;
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
    private String subject;
    private String senderEmail;
    private String recipientEmail;
    private String senderPhone;
    private String recipientPhone;
    private String message;
    private String html;
    private String attachment;
    private boolean requiredValidation;
    private NotificationType validationType;
}
