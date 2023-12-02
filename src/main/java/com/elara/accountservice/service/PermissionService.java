package com.elara.accountservice.service;

import com.elara.accountservice.auth.RequestUtil;
import com.elara.accountservice.domain.Company;
import com.elara.accountservice.domain.Group;
import com.elara.accountservice.domain.User;
import com.elara.accountservice.domain.UserGroup;
import com.elara.accountservice.dto.request.AssignUserGroupRequest;
import com.elara.accountservice.dto.request.CreateGroupRequest;
import com.elara.accountservice.dto.request.UpdateGroupRequest;
import com.elara.accountservice.dto.response.AssignUserGroupResponse;
import com.elara.accountservice.dto.response.CreateGroupResponse;
import com.elara.accountservice.dto.response.UpdateGroupResponse;
import com.elara.accountservice.enums.EntityStatus;
import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PermissionService {

  @PersistenceContext
  private final EntityManager entityManager;

  final ModelMapper modelMapper;
  final MessageService messageService;
  private final UserGroupService userGroupService;
  private final ApplicationRepository applicationRepository;
  private final GroupRepository groupRepository;
  private final UserGroupRepository userGroupRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;

  public PermissionService(EntityManager entityManager,
                           UserGroupService userGroupService,
                           ApplicationRepository applicationRepository,
                           ModelMapper modelMapper,
                           MessageService messageService,
                           GroupRepository groupRepository,
                           UserGroupRepository userGroupRepository,
                           CompanyRepository companyRepository,
                           UserRepository userRepository,
                           ApplicationPermissionRepository applicationPermissionRepository) {

    this.entityManager = entityManager;
    this.userGroupService = userGroupService;
    this.applicationRepository = applicationRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.applicationPermissionRepository = applicationPermissionRepository;
  }


  public CreateGroupResponse createGroup(CreateGroupRequest dto) {
    Company company = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Group existing = groupRepository.findByGroupNameAndCompanyCode(dto.getGroupName(), company.getCompanyCode());
    if (existing != null) {
      throw new AppException(messageService.getMessage("Group.Exist"));
    }

    Group newEntry = modelMapper.map(dto, Group.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());
    newEntry.setCompanyCode(company.getCompanyCode());
    newEntry = groupRepository.save(newEntry);
    CreateGroupResponse response = new CreateGroupResponse();
    response.setData(modelMapper.map(newEntry, CreateGroupResponse.Data.class));
    return response;
  }

  public UpdateGroupResponse updateGroup(UpdateGroupRequest dto) {
    Company company = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Group existing = groupRepository.findByGroupNameAndCompanyCode(dto.getGroupName(), company.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Group.NotFound"));
    }

    modelMapper.map(dto, existing);
    existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
    existing.setUpdatedAt(new Date());
    existing.setCompanyCode(company.getCompanyCode());
    existing = groupRepository.save(existing);
    UpdateGroupResponse response = new UpdateGroupResponse();
    response.setData(modelMapper.map(existing, UpdateGroupResponse.Data.class));
    return response;
  }

  public AssignUserGroupResponse assignGroupToUser(AssignUserGroupRequest dto) {
    Company existing = companyRepository.findByCompanyName(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<User> optionalUser = userRepository.findById(dto.getUserId());
    if (optionalUser.isEmpty()) {
      throw new AppException(messageService.getMessage("User.NotFound"));
    }

    User user = optionalUser.get();

    List<Long> deleteGroupIds = new ArrayList<>();

    List<UserGroup> userGroups = userGroupRepository.findByUserIdAndCompanyCode(user.getId(), dto.getCompanyCode());
    for (UserGroup userGroup : userGroups) {
      if (!dto.getGroupIds().contains(userGroup.getGroupId())) {
        deleteGroupIds.add(userGroup.getGroupId());
      }
    }

    for (Long groupId : dto.getGroupIds()) {
      Optional<Group> optionalGroup = groupRepository.findById(groupId);
      if (optionalGroup.isPresent()) {
        UserGroup existingUserGroup = userGroupRepository.findByCompanyCodeAndUserIdAndGroupId(dto.getCompanyCode(), dto.getUserId(), groupId);
        if (existingUserGroup == null) {
          userGroupRepository.save(UserGroup.builder()
                  .groupId(groupId)
                  .companyCode(dto.getCompanyCode())
                  .userId(user.getId())
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .build());
        }
      }
    }

    userGroupRepository.deleteByGroupIdIn(deleteGroupIds);

    return new AssignUserGroupResponse();
  }
}
