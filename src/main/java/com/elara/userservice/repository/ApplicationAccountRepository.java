package com.elara.userservice.repository;

import com.elara.userservice.domain.ApplicationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long>, JpaSpecificationExecutor<ApplicationAccount> {

  public List<ApplicationAccount> findByUserId(Long userId);
}
