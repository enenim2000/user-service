package com.elara.userservice.repository;

import com.elara.userservice.domain.AccountGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<AccountGroup, Long>, JpaSpecificationExecutor<AccountGroup> {

  AccountGroup findByNameAndCompany(String name, String company);
}
