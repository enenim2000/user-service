package com.elara.accountservice.repository;

import com.elara.accountservice.domain.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, JpaSpecificationExecutor<UserGroup> {
  List<UserGroup> findByUserId(long userId);

  @Modifying
  void deleteByGroupIdIn(List<Long> groupIds);
  List<UserGroup> findByUserIdAndCompanyCode(long userId, String companyCode);
  UserGroup findByCompanyCodeAndUserIdAndGroupId(String companyCode, long userId, long groupId);
}
