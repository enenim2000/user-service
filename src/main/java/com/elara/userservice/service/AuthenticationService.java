package com.elara.userservice.service;

import com.elara.userservice.auth.AuthToken;
import com.elara.userservice.auth.RequestUtil;
import com.elara.userservice.dto.request.*;
import com.elara.userservice.dto.response.*;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.enums.NotificationType;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
  final NotificationCacheService notificationCacheService;

  @Value("${spring.mail.username}")
  String senderMail;

  @Value("${sms.sender}")
  String senderSms;

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
                               UserGroupService userGroupService,
                               NotificationCacheService notificationCacheService) {
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
    this.notificationCacheService = notificationCacheService;
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
            .companyCode(newEntry.getCompanyCode())
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
            .html("Please use otp code {0} to verify your email")
            .senderEmail(senderMail)
            .recipientEmail(newEntry.getEmail())
            .companyCode(newEntry.getCompanyCode())
        .build());

    //Send otp to verify phone via Notification Service
    notificationService.sendSms(NotificationRequest.builder()
        .message("Please use otp code {0} to verify your phone number")
        .html(null)
        .senderPhone(senderSms)
        .recipientPhone(newEntry.getPhone())
        .companyCode(newEntry.getCompanyCode())
        .build());

    UserRegisterResponse response = new UserRegisterResponse();
    response.setResponseMessage(messageService.getMessage("User.Register.Success"));
    return response;
  }

  public UserLoginResponse login(UserLoginRequest dto) {
    Company company = companyRepository.findByClientId(RequestUtil.getClientId());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equalsIgnoreCase(company.getStatus())) {
      throw new AppException(messageService.getMessage("Company.Account.Disabled"));
    }

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), dto.getUsername());
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), dto.getUsername());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    UserLogin userLogin = userLoginRepository.findByUserId(user.getId());
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    if (!passwordEncoder.matches(dto.getPassword(), userLogin.getPassword())) {
      log.info("UserLogin password does not match for email: {}", user.getEmail());
      throw new AppException(messageService.getMessage("Login.Failed"));
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
    String token = RequestUtil.getToken();
    Claims claims = jwtTokens.parseJWT(token);
    Company company = companyRepository.findByCompanyCode(RequestUtil.getAuthToken().getCompanyCode());
    String username = claims.getSubject();

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), username);
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), username);
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    UserLogin userLogin = userLoginRepository.findByUserIdAndAccessToken(user.getId(), token);
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("Token.Not.Found"));
    }

    userLogin.setAccessToken("");
    userLogin.setRefreshToken("");

    userLoginRepository.save(userLogin);
    return new UserLogoutResponse();
  }

  public AccessTokenResponse getAccessTokenFromRefreshToken(AccessTokenRequest dto) {
    Claims claims = jwtTokens.parseRefreshJWT(dto.getRefreshToken());
    String companyCode = claims.getIssuer();
    Company company = companyRepository.findByCompanyCode(companyCode);
    String username = claims.getSubject();

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), username);
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), username);
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    UserLogin userLogin = userLoginRepository.findByUserId(user.getId());
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    if (!dto.getRefreshToken().equals(userLogin.getRefreshToken())) {
      log.info("Refresh token not match for userId:{}", username);
      throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
    }

    List<String> audience = applicationService.getAudience(user.getId());

    AuthToken authToken = modelMapper.map(user, AuthToken.class);
    String accessToken = jwtTokens.generateAccessToken(company, username);
    String refreshToken = jwtTokens.generateRefreshToken(company);
    authToken.setAccessToken(accessToken);
    authToken.setAudience(audience);
    authToken.setUsername(username);
    authToken.setRefreshToken(refreshToken);
    authToken.setExpires(jwtTokens.parseJWT(accessToken).getExpiration().toString());

    userLogin.setAccessToken(accessToken);
    userLogin.setRefreshToken(refreshToken);
    userLoginRepository.save(userLogin);

    return AccessTokenResponse.builder()
            .data(authToken)
            .build();

  }

  private boolean isAuthenticated(String token, long userId) {
    UserLogin userLogin = userLoginRepository.findByUserIdAndAccessToken(userId, token);
    if (userLogin == null) {
      throw new AppException(messageService.getMessage("Token.Not.Found"));
    }

    if (!token.equals(userLogin.getAccessToken())) {
      log.info("Token not match for userId:{}", userId);
      throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
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

  public TokenVerifyResponse verifyToken(TokenVerifyRequest request) {
    TokenVerifyResponse response = new TokenVerifyResponse();
    response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
    response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));

    //Client id of the service on application table
    String serviceClientId = request.getServiceClientId();

    Application application = applicationService.getByPublicKey(serviceClientId);
    if (application == null) {
      throw new AppException(messageService.getMessage("App.Setup.NotFound"));
    }

    //Token forwarded by API Gateway or frontend client
    String userToken = request.getToken();

    //SHA 256 hash of service-name, METHOD, path uri forwarded by the called service
    String endpoint = request.getPermissionId();

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
        response.setResponseCode(ResponseCode.FORBIDDEN.getValue());
        response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));
      }
    } else {
      response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
      response.setResponseMessage(messageService.getMessage("Auth.Forbidden"));
    }

    return response;
  }

  public OtpVerifyResponse verifyOtp(String otp, NotificationType notificationType) {
    String companyCode = RequestUtil.getAuthToken().getCompanyCode();
    String username = RequestUtil.getAuthToken().getUsername();
    User user = userRepository.findByCompanyCodeAndEmailOrPhone(companyCode, username);
    if (user == null) {
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    boolean isValidOtp = notificationCacheService.isValid(companyCode, user.getId(), notificationType, otp);
    OtpVerifyResponse response = new OtpVerifyResponse();
    if (isValidOtp) {
      response.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
      response.setResponseMessage(messageService.getMessage("Otp.Verify.Success"));
      return response;
    }

    throw new AppException(messageService.getMessage("Otp.Verify.Fail"));
  }

  public OtpResendResponse resendPhoneOtp(String otp) {
    OtpVerifyResponse response = verifyOtp(otp, NotificationType.PhoneVerify);
    OtpResendResponse resp = new OtpResendResponse();
    resp.setResponseCode(response.getResponseCode());
    resp.setResponseMessage(response.getResponseMessage());
    return resp;
  }

  public OtpResendResponse resendEmailOtp(String otp) {
    OtpVerifyResponse response = verifyOtp(otp, NotificationType.EmailVerify);
    OtpResendResponse resp = new OtpResendResponse();
    resp.setResponseCode(response.getResponseCode());
    resp.setResponseMessage(response.getResponseMessage());
    return resp;
  }

  public OtpVerifyResponse verifyEmailOtp(String otp) {
    OtpVerifyResponse response = verifyOtp(otp, NotificationType.EmailVerify);
    OtpVerifyResponse resp = new OtpVerifyResponse();
    resp.setResponseCode(response.getResponseCode());
    resp.setResponseMessage(response.getResponseMessage());
    return resp;
  }

  public OtpVerifyResponse verifyPhoneOtp(String otp) {
    OtpVerifyResponse response = verifyOtp(otp, NotificationType.PhoneVerify);
    OtpVerifyResponse resp = new OtpVerifyResponse();
    resp.setResponseCode(response.getResponseCode());
    resp.setResponseMessage(response.getResponseMessage());
    return resp;
  }
}
