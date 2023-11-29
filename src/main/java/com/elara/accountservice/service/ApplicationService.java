package com.elara.accountservice.service;

import com.elara.accountservice.domain.Application;
import com.elara.accountservice.domain.ApplicationPermission;
import com.elara.accountservice.repository.ApplicationPermissionRepository;
import com.elara.accountservice.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ApplicationService {

  @PersistenceContext
  private EntityManager entityManager;
  
  private final UserGroupService userGroupService;
  private final ApplicationRepository applicationRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;

  public ApplicationService(EntityManager entityManager,
                            UserGroupService userGroupService,
                            ApplicationRepository applicationRepository,
                            ApplicationPermissionRepository applicationPermissionRepository) {

    this.entityManager = entityManager;
    this.userGroupService = userGroupService;
    this.applicationRepository = applicationRepository;
    this.applicationPermissionRepository = applicationPermissionRepository;
  }

  public List<String> getAudience(long userId) {
    List<String> audience = new ArrayList<>();
    try {
      List<Long> groupIds = userGroupService.groupIds(userId);
      String groupIdString = StringUtils.join(groupIds, ',');
      TypedQuery<Object[]> query = entityManager.createQuery(
          "Select a.app_name from user_group_permission ugp inner join application_permission ap on ugp.application_permission_id = ap.id inner join application a on ap.application_id = a.id group by a.app_name where ugp.group_id in (" + groupIdString + ")", Object[].class);
      List<Object[]> results = query.getResultList();
      log.info("result.size(): {}", results.size());
      for (Object[] array : results) {
        audience.add((String)array[0]);
      }
      return audience;
    } catch (Exception e) {
      log.error("Error while attempting to get audience", e);
    }

    return audience;
  }

  public ApplicationPermission getByPermissionId(String permissionId) {
    return applicationPermissionRepository.findByPermissionId(permissionId);
  }

  public Application getByPublicKey(String serviceClientId) {
    return applicationRepository.findByPublicKey(serviceClientId);
  }
}
