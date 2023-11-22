package com.elara.userservice.repository;

import com.elara.userservice.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApplicationRepository  extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

  Application findByAppName(String appName);
}
