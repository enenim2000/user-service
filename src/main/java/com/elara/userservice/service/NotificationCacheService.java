package com.elara.userservice.service;

import com.elara.userservice.enums.NotificationType;
import com.elara.userservice.model.NotificationCache;
import com.elara.userservice.repository.NotificationCacheRepository;
import com.elara.userservice.util.HashUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationCacheService {

  private final NotificationCacheRepository cacheRepository;

  @Value("${transaction.otp.expiry}")
  private String transactionExpiry;

  @Value("${verify.otp.expiry}")
  private String verifyOtpExpiry;

  public NotificationCacheService(NotificationCacheRepository cacheRepository) {
    this.cacheRepository = cacheRepository;
  }

  public void put(String companyCode, long userId, NotificationType notificationType, String otp) {
    Date now = new Date();
    long MINUTES = 60 * 1000; // in milli-seconds.
    int expiry;

    if (NotificationType.TransactionVerify.name().equalsIgnoreCase(notificationType.name())) {
      expiry = Integer.parseInt(transactionExpiry);
    } else {
      expiry = Integer.parseInt(verifyOtpExpiry);
    }

    Date newDate = new Date(now.getTime() + expiry * MINUTES);

    NotificationCache notificationCache = NotificationCache.builder().build();
    notificationCache.setNotificationType(notificationCache.getNotificationType());
    notificationCache.setOtp(otp);
    notificationCache.setToken(HashUtil.getHash(companyCode + userId + notificationType.name() + otp));
    notificationCache.setExpiry(newDate);
    notificationCache.setUserId(userId);
    notificationCache.setCompanyCode(companyCode);
    cacheRepository.save(notificationCache);
  }

  private NotificationCache get(String companyCode, long userId, NotificationType notificationType, String otp) {
    String token = HashUtil.getHash(companyCode + userId + notificationType.name() + otp);
    return cacheRepository.findByToken(token);
  }

  public boolean isValid(String companyCode, long userId, NotificationType notificationType, String otp) {
    NotificationCache cache = get(companyCode, userId, notificationType, otp);
    if (cache == null) {
      return false;
    }
    return new Date().before(cache.getExpiry());
  }
}
