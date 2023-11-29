package com.elara.userservice.repository;

import com.elara.userservice.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository  extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

  Application findByAppName(String appName);

  Application findByPublicKey(String serviceClientId);
}
