package com.elara.userservice.service;

import com.elara.userservice.model.User;
import com.elara.userservice.model.UserGroup;
import com.elara.userservice.repository.UserGroupRepository;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApplicationService {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private UserGroupRepository userGroupRepository;

  public List<String> getAudience(long userId) {
    List<Long> groupIds = groupIds(userId);
    String groupIdString = StringUtils.join(groupIds, ',');
    Query query = entityManager.createNativeQuery(
        "Select a.app_name from user_group_permission ugp inner join application_permission ap on ugp.application_permission_id = ap.id inner join application a on ap.application_id = a.id group by a.app_name where ugp.group_id in (" + groupIdString + ")");
    List<String> audience = new ArrayList<>();
    List<Object[]> result = (List<Object[]>) query.getResultList();
    log.info("result.size(): {}", result.size());
    for (Object[] array : result) {
      audience.add((String)array[0]);
    }
    return audience;
  }

  private List<Long> groupIds(long userId) {
    List<Long> groupIds = new ArrayList<>();
    List<UserGroup> groups = userGroupRepository.findByUserId(userId);
    for (UserGroup group : groups) {
      groupIds.add(group.getGroupId());
    }
    return groupIds;
  }
}
