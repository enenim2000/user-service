package com.elara.userservice.repository;

import com.elara.userservice.domain.NotificationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface NotificationCacheRepository extends JpaRepository<NotificationCache, Long>, JpaSpecificationExecutor<NotificationCache> {

  NotificationCache findByToken(String token);

  @Modifying
  @Query("DELETE FROM NotificationCache n WHERE n.expiry <= :now")
  void deleteExpiredOtp(@Param("now") Date now);
}
