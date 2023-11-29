package com.elara.accountservice.repository;

import com.elara.accountservice.domain.AccountGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<AccountGroup, Long>, JpaSpecificationExecutor<AccountGroup> {

  AccountGroup findByNameAndCompany(String name, String company);
}
