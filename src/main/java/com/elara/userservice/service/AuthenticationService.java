package com.elara.userservice.service;

import com.elara.userservice.auth.AuthToken;
import com.elara.userservice.auth.RequestUtil;
import com.elara.userservice.dto.request.NotificationRequest;
import com.elara.userservice.dto.request.UserLoginRequest;
import com.elara.userservice.dto.request.UserRegisterRequest;
import com.elara.userservice.dto.response.UserLoginResponse;
import com.elara.userservice.dto.response.UserLogoutResponse;
import com.elara.userservice.dto.response.UserRegisterResponse;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.enums.UserType;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.model.Company;
import com.elara.userservice.model.Group;
import com.elara.userservice.model.User;
import com.elara.userservice.model.UserGroup;
import com.elara.userservice.model.UserLogin;
import com.elara.userservice.repository.CompanyRepository;
import com.elara.userservice.repository.GroupRepository;
import com.elara.userservice.repository.UserGroupRepository;
import com.elara.userservice.repository.UserLoginRepository;
import com.elara.userservice.repository.UserRepository;
import com.elara.userservice.util.JWTTokens;
import com.elara.userservice.util.PasswordEncoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AuthenticationService {

  final UserRepository userRepository;
  final CompanyRepository companyRepository;
  final GroupRepository groupRepository;
  final UserGroupRepository userGroupRepository;
  final UserLoginRepository userLoginRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;
  final NotificationService notificationService;
  final PasswordEncoder passwordEncoder;
  final JWTTokens jwtTokens;
  final ApplicationService applicationService;

  public AuthenticationService(UserRepository userRepository,
                                CompanyRepository companyRepository,
                                GroupRepository groupRepository,
                                UserGroupRepository userGroupRepository,
                                UserLoginRepository userLoginRepository,
                                 ModelMapper modelMapper,
                                 MessageService messageService,
                                  NotificationService notificationService,
                                  PasswordEncoder passwordEncoder,
                                 JWTTokens jwtTokens, ApplicationService applicationService) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.userLoginRepository = userLoginRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.notificationService = notificationService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokens = jwtTokens;
    this.applicationService = applicationService;
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

    UserLogin userLogin = userLoginRepository.save(UserLogin.builder()
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
    userGroup.setUserId(String.valueOf(newEntry.getId()));
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
    String token = jwtTokens.createJWT(company);
    authToken.setToken(token);
    authToken.setAudience(audience);
    authToken.setRefreshToken(jwtTokens.parseJWT(token));
    authToken.setExpires(jwtTokens.parseJWT(token).getExpiration().toString());

    //TODO generate refresh token
    //TODO save token and refresh token to UserLogin table DB

    return UserLoginResponse.builder()
            .data(authToken)
            .build();
  }

  public UserLogoutResponse logout() {
    return null;
  }

  protected boolean isValidToken() {
    String token = RequestUtil.getToken();
    token = token.replace("Bearer ", "").trim();
    AuthToken authToken = new AuthToken();
    try {
      Claims claims = jwtTokens.parseJWT(token);
      String username = claims.getId();
      String companyCode = RequestUtil.getAuthToken().getCompanyCode();
      User user = userRepository.findByCompanyCodeAndEmailOrPhone(companyCode, username);

      UserLogin userLogin = user.;
      return null;
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
             IllegalArgumentException ex) {
      log.error("Token Message: ", ex);
    }

    return userInfo;
  }
}
