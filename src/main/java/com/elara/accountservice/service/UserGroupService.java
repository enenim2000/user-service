package com.elara.accountservice.service;

import com.elara.accountservice.domain.UserGroup;
import com.elara.accountservice.repository.UserGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserGroupService {

  final UserGroupRepository userGroupRepository;


  public UserGroupService(UserGroupRepository userGroupRepository) {
    this.userGroupRepository = userGroupRepository;
  }

  public List<Long> groupIds(long userId) {
    List<Long> groupIds = new ArrayList<>();
    List<UserGroup> groups = userGroupRepository.findByUserId(userId);
    for (UserGroup group : groups) {
      groupIds.add(group.getGroupId());
    }
    return groupIds;
  }
}
