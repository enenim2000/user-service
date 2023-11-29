package com.elara.userservice.service;

import com.elara.userservice.auth.RequestUtil;
import com.elara.userservice.dto.request.NotificationRequest;
import com.elara.userservice.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

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

  public void sendNotification(NotificationRequest dto) {
    if (dto != null) {
      if (dto.isRequiredValidation()) {
        String code = generateOtp();
        dto.setMessage(dto.getMessage().replace("{0}", code));
        if (StringUtils.hasText(dto.getHtml())) {
          dto.setHtml(dto.getHtml().replace("{0}", code));
        }
        cacheService.put(dto.getCompanyCode(), RequestUtil.getUser().getId(), dto.getValidationType(), code);
      }

      if (StringUtils.hasText(dto.getRecipientEmail())) {
        sendEmail(dto);
      }

      if (StringUtils.hasText(dto.getRecipientPhone())) {
        sendSms(dto);
      }
    }
  }

  private String generateOtp() {
    // It will generate 6 digit random Number.
    // from 0 to 999999
    Random rnd = new Random();
    int number = rnd.nextInt(999999);

    // this will convert any number sequence into 6 character.
    return String.format("%06d", number);
  }
}
