package com.elara.userservice.repository;

import com.elara.userservice.domain.UserGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserGroupPermissionRepository extends JpaRepository<UserGroupPermission, Long>, JpaSpecificationExecutor<UserGroupPermission> {

  List<UserGroupPermission> findByGroupIdIn(List<Long> groupIds);

}
