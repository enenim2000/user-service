package com.elara.userservice.repository;

import com.elara.userservice.model.ApplicationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long>, JpaSpecificationExecutor<ApplicationAccount> {

  public List<ApplicationAccount> findByUserId(Long userId);
}