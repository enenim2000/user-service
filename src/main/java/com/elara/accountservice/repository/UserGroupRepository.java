package com.elara.accountservice.repository;

import com.elara.accountservice.domain.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, JpaSpecificationExecutor<UserGroup> {
  List<UserGroup> findByUserId(long userId);
}