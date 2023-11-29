package com.elara.accountservice.repository;

import com.elara.accountservice.domain.UserGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserGroupPermissionRepository extends JpaRepository<UserGroupPermission, Long>, JpaSpecificationExecutor<UserGroupPermission> {

  List<UserGroupPermission> findByGroupIdIn(List<Long> groupIds);

}