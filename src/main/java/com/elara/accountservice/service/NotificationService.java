package com.elara.accountservice.service;

import com.elara.accountservice.auth.RequestUtil;
import com.elara.accountservice.dto.request.NotificationRequest;
import com.elara.accountservice.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class NotificationService {

  private final MailService mailService;
  private final SmsService smsService;
  private final NotificationCacheService cacheService;
  private final CompanyRepository companyRepository;

  public NotificationService(MailService mailService,
                             SmsService smsService,
                             NotificationCacheService cacheService,
                             CompanyRepository companyRepository) {
    this.mailService = mailService;
    this.smsService = smsService;
    this.cacheService = cacheService;
    this.companyRepository = companyRepository;
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

  public void sendNotification(NotificationRequest dto, String otp) {
    if (dto != null) {
      if (dto.isRequiredValidation()) {
        dto.setMessage(dto.getMessage().replace("{0}", otp));
        if (StringUtils.hasText(dto.getHtml())) {
          dto.setHtml(dto.getHtml().replace("{0}", otp));
        }
        cacheService.put(dto.getCompanyCode(), RequestUtil.getUser().getId(), dto.getValidationType(), otp);
      }

      if (StringUtils.hasText(dto.getRecipientEmail())) {
        sendEmail(dto);
      }

      if (StringUtils.hasText(dto.getRecipientPhone())) {
        sendSms(dto);
      }
    }
  }
}
