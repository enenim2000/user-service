package com.elara.userservice.service;

import com.elara.userservice.dto.request.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class NotificationService {

  private final MailService mailService;
  private final SmsService smsService;

  public NotificationService(MailService mailService,
      SmsService smsService) {
    this.mailService = mailService;
    this.smsService = smsService;
  }

  @Async("taskExecutor")
  public void sendEmail(NotificationRequest dto) {
    log.info("Sending... email to notification service");
    mailService.sendNotification(dto);
  }

  @Async("taskExecutor")
  public void sendSms(NotificationRequest dto) {
    log.info("Sending... sms to notification service");
    smsService.sendMessage(dto);
  }

  public void sendNotification(NotificationRequest dto) {
    if (dto != null) {
      if (StringUtils.hasText(dto.getRecipientEmail())) {
        sendEmail(dto);
      }

      if (StringUtils.hasText(dto.getRecipientPhone())) {
        sendSms(dto);
      }
    }
  }
}
