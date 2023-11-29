package com.elara.accountservice.repository;

import com.elara.accountservice.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository  extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

  Application findByAppName(String appName);

  Application findByPublicKey(String serviceClientId);
}
