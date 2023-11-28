package com.elara.userservice.repository;

import com.elara.userservice.model.NotificationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationCacheRepository extends JpaRepository<NotificationCache, Long>, JpaSpecificationExecutor<NotificationCache> {

  NotificationCache findByToken(String token);

}
