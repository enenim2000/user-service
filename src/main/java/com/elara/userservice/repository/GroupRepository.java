package com.elara.userservice.repository;

import com.elara.userservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

  Group findByGroupNameAndCompanyCode(String groupName, String companyCode);
}
