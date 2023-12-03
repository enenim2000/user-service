package com.elara.accountservice.repository;

import com.elara.accountservice.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

  Group findByGroupNameAndCompanyCode(String groupName, String companyCode);
}
