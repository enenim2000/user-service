package com.elara.accountservice.service;

import com.elara.accountservice.auth.RequestUtil;
import com.elara.accountservice.domain.*;
import com.elara.accountservice.dto.request.*;
import com.elara.accountservice.dto.response.*;
import com.elara.accountservice.enums.EntityStatus;
import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.repository.*;
import com.elara.accountservice.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PermissionService {

  final ModelMapper modelMapper;
  final MessageService messageService;
  private final ApplicationRepository applicationRepository;
  private final GroupRepository groupRepository;
  private final UserGroupRepository userGroupRepository;
  private final UserGroupPermissionRepository userGroupPermissionRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final ApplicationAccountRepository applicationAccountRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;

  public PermissionService(
          ApplicationRepository applicationRepository,
          ModelMapper modelMapper,
          MessageService messageService,
          GroupRepository groupRepository,
          UserGroupRepository userGroupRepository,
          UserGroupPermissionRepository userGroupPermissionRepository,
          CompanyRepository companyRepository,
          UserRepository userRepository,
          ApplicationAccountRepository applicationAccountRepository,
          ApplicationPermissionRepository applicationPermissionRepository) {
    this.applicationRepository = applicationRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.userGroupPermissionRepository = userGroupPermissionRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.applicationAccountRepository = applicationAccountRepository;
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

    userGroupRepository.deleteByCompanyCodeAndUserIdAndGroupIdIn(dto.getCompanyCode(), user.getId(), deleteGroupIds);

    return new AssignUserGroupResponse();
  }

  public AssignUserPermissionResponse assignPermissionToUser(AssignUserPermissionRequest dto) {
    Company existing = companyRepository.findByCompanyName(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<User> optionalUser = userRepository.findById(dto.getUserId());
    if (optionalUser.isEmpty()) {
      throw new AppException(messageService.getMessage("User.NotFound"));
    }

    User user = optionalUser.get();

    List<String> deletePermissionIds = new ArrayList<>();

    List<ApplicationAccount> userPermissions = applicationAccountRepository.findByUserIdAndCompanyCode(user.getId(), dto.getCompanyCode());
    for (ApplicationAccount userPermission : userPermissions) {
      if (!dto.getPermissionIds().contains(userPermission.getPermissionId())) {
        deletePermissionIds.add(userPermission.getPermissionId());
      }
    }

    for (String permissionId : dto.getPermissionIds()) {
      ApplicationPermission applicationPermission = applicationPermissionRepository.findByPermissionId(permissionId);
      if (applicationPermission != null) {
        ApplicationAccount applicationAccount = applicationAccountRepository.findByCompanyCodeAndUserIdAndPermissionId(dto.getCompanyCode(), dto.getUserId(), permissionId);
        if (applicationAccount == null) {
          applicationAccountRepository.save(ApplicationAccount.builder()
                  .permissionId(permissionId)
                  .companyCode(dto.getCompanyCode())
                  .userId(user.getId())
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .status(EntityStatus.Enabled.name())
                  .build());
        }
      }
    }

    applicationAccountRepository.deleteByCompanyCodeAndUserIdAndPermissionIdIn(dto.getCompanyCode(), user.getId(), deletePermissionIds);

    return new AssignUserPermissionResponse();
  }

  public SyncPermissionResponse syncApplicationPermission(SyncPermissionRequest dto) {
    Application application = applicationRepository.findByAppName(dto.getAppName());
    if (application == null) {
      throw new AppException(messageService.getMessage("App.NotFound"));
    }

    for (SyncPermissionRequest.Data permission : dto.getPermissions()) {

      //Hash SHA 256 of appName,http method,uri e.g user-service,GET,/api/user/logout
      String permissionConcat = dto.getAppName().toLowerCase() + permission.getHttpMethod().toUpperCase() + permission.getUriPath().toLowerCase();
      String permissionId = HashUtil.getHash(permissionConcat);
      ApplicationPermission applicationPermission = applicationPermissionRepository.findByPermissionId(permissionId);
      if (applicationPermission == null) {
        applicationPermissionRepository.save(ApplicationPermission.builder()
                        .applicationId(application.getId())
                        .permissionId(permissionId)
                        .description(permission.getDescription())
                        .isSecured(permission.isSecured())
                        .httpMethod(permission.getHttpMethod())
                        .permission(permission.getPermission())
                        .uriPath(permission.getUriPath())
                        .status(EntityStatus.Enabled.name())
                        .createdAt(new Date())
                        .createdBy(dto.getAppName())
                        .build());
      } else {
        applicationPermission.setPermission(permission.getPermission());
        applicationPermission.setDescription(permission.getDescription());
        applicationPermission.setSecured(permission.isSecured());
        applicationPermission.setUpdatedAt(new Date());
        applicationPermission.setUpdatedBy(dto.getAppName());
        applicationPermissionRepository.save(applicationPermission);
      }
    }

    return new SyncPermissionResponse();
  }

  public AssignGroupPermissionResponse assignPermissionToGroup(AssignGroupPermissionRequest dto) {
    Company existing = companyRepository.findByCompanyName(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<Group> optionalGroup = groupRepository.findById(dto.getGroupId());
    if (optionalGroup.isEmpty()) {
      throw new AppException(messageService.getMessage("Group.NotFound"));
    }

    Group group = optionalGroup.get();

    List<Long> deletePermissionIds = new ArrayList<>();

    List<UserGroupPermission> groupPermissions = userGroupPermissionRepository.findByGroupIdAndCompanyCode(group.getId(), dto.getCompanyCode());
    for (UserGroupPermission groupPermission : groupPermissions) {
      if (!dto.getPermissionIds().contains(groupPermission.getApplicationPermissionId())) {
        deletePermissionIds.add(groupPermission.getApplicationPermissionId());
      }
    }

    for (Long applicationPermissionId : dto.getPermissionIds()) {
      Optional<ApplicationPermission> applicationPermission = applicationPermissionRepository.findById(applicationPermissionId);
      if (applicationPermission.isPresent()) {
        UserGroupPermission userGroupPermission = userGroupPermissionRepository.findByCompanyCodeAndGroupIdAndApplicationPermissionId(dto.getCompanyCode(), dto.getGroupId(), applicationPermissionId);
        if (userGroupPermission == null) {
          userGroupPermissionRepository.save(UserGroupPermission.builder()
                  .companyCode(dto.getCompanyCode())
                  .groupId(group.getId())
                  .applicationPermissionId(applicationPermissionId)
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .status(EntityStatus.Enabled.name())
                  .build());
        }
      }
    }

    userGroupPermissionRepository.deleteByCompanyCodeAndGroupIdAndApplicationPermissionIdIn(dto.getCompanyCode(), group.getId(), deletePermissionIds);

    return new AssignGroupPermissionResponse();
  }
}
