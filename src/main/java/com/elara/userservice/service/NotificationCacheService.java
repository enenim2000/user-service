package com.elara.userservice.service;

import com.elara.userservice.enums.NotificationType;
import com.elara.userservice.domain.NotificationCache;
import com.elara.userservice.repository.NotificationCacheRepository;
import com.elara.userservice.util.HashUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
    long MINUTES = 60 * 1000;
    int expiry;

    if (NotificationType.TransactionVerify.name().equalsIgnoreCase(notificationType.name())) {
      expiry = Integer.parseInt(transactionExpiry);
    } else {
      expiry = Integer.parseInt(verifyOtpExpiry);
    }

    Date newDate = new Date(now.getTime() + expiry * MINUTES);

    String token = HashUtil.getHash(companyCode + userId + notificationType.name() + otp);
    NotificationCache notificationCache = cacheRepository.findByToken(token);

    if (notificationCache == null) {
      notificationCache = new NotificationCache();
    }

    notificationCache.setNotificationType(notificationCache.getNotificationType());
    notificationCache.setOtp(otp);
    notificationCache.setToken(token);
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

  @Async("taskExecutor")
  public void deleteExpiredOtp() {
    cacheRepository.deleteExpiredOtp(new Date());
  }
}
