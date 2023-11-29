package com.elara.accountservice.repository;

import com.elara.accountservice.domain.ApplicationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long>, JpaSpecificationExecutor<ApplicationAccount> {

  public List<ApplicationAccount> findByUserId(Long userId);
}
