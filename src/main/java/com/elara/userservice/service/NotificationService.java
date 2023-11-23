package com.elara.userservice.service;

import com.elara.userservice.dto.request.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

  @Async
  public void sendEmail(NotificationRequest dto) {
    log.info("Sending... email to notification service");
  }

  @Async
  public void sendSms(NotificationRequest dto) {
    log.info("Sending... sms to notification service");
  }
}
