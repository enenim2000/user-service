package com.elara.userservice.service;

import com.elara.userservice.auth.AuthToken;
import com.elara.userservice.auth.RequestUtil;
import com.elara.userservice.dto.request.NotificationRequest;
import com.elara.userservice.dto.request.UserLoginRequest;
import com.elara.userservice.dto.request.UserRegisterRequest;
import com.elara.userservice.dto.response.TokenVerifyResponse;
import com.elara.userservice.dto.response.UserLoginResponse;
import com.elara.userservice.dto.response.UserLogoutResponse;
import com.elara.userservice.dto.response.UserRegisterResponse;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.enums.ResponseCode;
import com.elara.userservice.enums.UserType;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.exception.UnAuthorizedException;
import com.elara.userservice.model.*;
import com.elara.userservice.repository.*;
import com.elara.userservice.util.JWTTokens;
import com.elara.userservice.util.PasswordEncoder;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AuthenticationService {

  final UserRepository userRepository;
  final CompanyRepository companyRepository;
  final GroupRepository groupRepository;
  final UserGroupRepository userGroupRepository;
  final UserGroupPermissionRepository userGroupPermissionRepository;
  final UserLoginRepository userLoginRepository;
  final ApplicationAccountRepository applicationAccountRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;
  final NotificationService notificationService;
  final PasswordEncoder passwordEncoder;
  final JWTTokens jwtTokens;
  final ApplicationService applicationService;
  final UserGroupService userGroupService;

  public AuthenticationService(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               GroupRepository groupRepository,
                               UserGroupRepository userGroupRepository,
                               UserGroupPermissionRepository userGroupPermissionRepository,
                               UserLoginRepository userLoginRepository,
                               ApplicationAccountRepository applicationAccountRepository,
                               ModelMapper modelMapper,
                               MessageService messageService,
                               NotificationService notificationService,
                               PasswordEncoder passwordEncoder,
                               JWTTokens jwtTokens,
                               ApplicationService applicationService,
                               UserGroupService userGroupService) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.userGroupPermissionRepository = userGroupPermissionRepository;
    this.userLoginRepository = userLoginRepository;
    this.applicationAccountRepository = applicationAccountRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.notificationService = notificationService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokens = jwtTokens;
    this.applicationService = applicationService;
    this.userGroupService = userGroupService;
  }

  public UserRegisterResponse registerUser(UserRegisterRequest dto) {
    User existing  = userRepository.findByEmail(dto.getEmail());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Email.Exist"));
    }

    existing = userRepository.findByPhone(dto.getPhone());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Phone.Exist"));
    }

    User newEntry = userRepository.save(User.builder()
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .companyCode(RequestUtil.getAuthToken().getCompanyCode())
            .isEmailVerified(false)
            .isPhoneVerified(false)
            .status(EntityStatus.Enabled.name())
            .createdBy(UserType.Customer.name())
            .createdAt(new Date())
            .build());

    userLoginRepository.save(UserLogin.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .status(EntityStatus.Disabled.name())//Until phone and email is verified
            .createdAt(new Date())
            .userId(newEntry.getId())
            .build());

    String groupName = UserType.Customer.name();
    Group group = groupRepository.findByGroupNameAndCompanyCode(groupName,
        newEntry.getCompanyCode());

    if (group == null) {
      throw new AppException(messageService.getMessage("Group.Not.Found").replace("{0}", groupName));
    }

    UserGroup userGroup =  new UserGroup();
    userGroup.setUserId(newEntry.getId());
    userGroup.setGroupId(group.getId());
    userGroupRepository.save(userGroup);

    //Send otp to verify email via Notification Service
    notificationService.sendEmail(NotificationRequest.builder()
            .message("Please use otp code {0} to verify your email")
            .html(null)
            .to(newEntry.getEmail())
            .appId("user-service")
            .companyCode(newEntry.getCompanyCode())
            .userId(newEntry.getEmail())
        .build());

    //Send otp to verify phone via Notification Service
    notificationService.sendSms(NotificationRequest.builder()
        .message("Please use otp code {0} to verify your phone number")
        .html(null)
        .to(newEntry.getPhone())
        .appId("user-service")
        .userId(newEntry.getPhone())
        .companyCode(newEntry.getCompanyCode())
        .build());

    UserRegisterResponse response = new UserRegisterResponse();
    response.setResponseMessage(messageService.getMessage("User.Register.Success"));
    return response;
  }

  public UserLoginResponse login(UserLoginRequest dto) {
    Company company = companyRepository.findByClientId(RequestUtil.getClientId());
    if (company == null) {
      throw new AppException("Company.Not.Found");
    }

    if (EntityStatus.Disabled.name().equalsIgnoreCase(company.getStatus())) {
      throw new AppException("Company.Account.Disabled");
    }

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), dto.getUsername());
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), dto.getUsername());
      throw new AppException("Login.Failed");
    }

    UserLogin userLogin = userLoginRepository.findByUserId(user.getId());
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException("Login.Failed");
    }

    if (!passwordEncoder.matches(dto.getPassword(), userLogin.getPassword())) {
      log.info("UserLogin password does not match for email: {}", user.getEmail());
      throw new AppException("Login.Failed");
    }

    List<String> audience = applicationService.getAudience(user.getId());

    AuthToken authToken = modelMapper.map(user, AuthToken.class);
    String accessToken = jwtTokens.generateAccessToken(company, dto.getUsername());
    String refreshToken = jwtTokens.generateRefreshToken(company);
    authToken.setAccessToken(accessToken);
    authToken.setAudience(audience);
    authToken.setUsername(dto.getUsername());
    authToken.setRefreshToken(refreshToken);
    authToken.setExpires(jwtTokens.parseJWT(accessToken).getExpiration().toString());

    userLogin.setAccessToken(accessToken);
    userLogin.setRefreshToken(refreshToken);
    userLoginRepository.save(userLogin);

    return UserLoginResponse.builder()
            .data(authToken)
            .build();
  }

  public UserLogoutResponse logout() {
    return null;
  }

  private boolean isAuthenticated(String token, long userId) {
    UserLogin userLogin = userLoginRepository.findByUserIdAndAccessToken(userId, token);
    if (userLogin == null) {
      throw new AppException(messageService.getMessage("Token.Not.Found"));
    }

    return true;
  }

  /**
   *
   * @param endpoint is a hash value of SHA 256 of appName,http method,uri e.g user-service,GET,/api/user/logout
   * @return true if the user has the permission to call the endpoint, otherwise return false
   */
  public boolean isAuthorized(String endpoint, long userId) {
    ApplicationPermission resource = applicationService.getByPermissionId(endpoint);

    if (!resource.isSecured()) {
      return true;
    }

    List<ApplicationAccount> userPermissions = applicationAccountRepository.findByUserId(userId);
    for (ApplicationAccount userPermission : userPermissions) {
      if (resource.getId().equals(userPermission.getId())) {
        return true;
      }
    }

    List<Long> groupIds = userGroupService.groupIds(userId);
    List<UserGroupPermission> groupPermissions = userGroupPermissionRepository.findByGroupIdIn(groupIds);
    List<Long> applicationPermissionIds = new ArrayList<>();
    for (UserGroupPermission groupPermission : groupPermissions) {
      applicationPermissionIds.add(groupPermission.getApplicationPermissionId());
    }

    return applicationPermissionIds.contains(resource.getId());
  }

  public TokenVerifyResponse verifyToken(HttpServletRequest request) {
    TokenVerifyResponse response = new TokenVerifyResponse();
    response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
    response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));

    //Client id of the service on application table
    String serviceClientId = request.getHeader("x-auth-client-id");

    Application application = applicationService.getByPublicKey(serviceClientId);
    if (application == null) {
      throw new AppException(messageService.getMessage("App.Setup.NotFound"));
    }

    //Token forwarded by API Gateway or frontend client
    String userToken = request.getHeader("x-auth-client-token");

    //SHA 256 hash of service-name, METHOD, path uri forwarded by the called service
    String endpoint = request.getHeader("x-auth-permission-id");

    Claims claims = jwtTokens.parseJWT(userToken);
    String username = claims.getSubject();
    String companyCode = claims.getIssuer();

    Company company = companyRepository.findByCompanyCode(companyCode);
    if (company == null) {
      throw new AppException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("Company.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equals(company.getStatus())) {
      throw new AppException(messageService.getMessage("Company.Account.Disabled"));
    }

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(companyCode, username);

    if (user == null) {
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equals(user.getStatus())) {
      throw new UnAuthorizedException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("User.Account.Disabled"));
    }

    if (isAuthenticated(userToken, user.getId())) {
      if (isAuthorized(endpoint, user.getId())) {
        response.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
        response.setResponseMessage(messageService.getMessage("Auth.Successful"));
      } else {
        response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
        response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));
      }
    } else {
      response.setResponseCode(ResponseCode.FORBIDDEN.getValue());
      response.setResponseMessage(messageService.getMessage("Auth.Forbidden"));
    }

    return response;
  }
}
