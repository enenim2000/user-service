package com.elara.userservice.service;

import com.elara.userservice.auth.AuthToken;
import com.elara.userservice.auth.RequestUtil;
import com.elara.userservice.dto.request.UserLoginRequest;
import com.elara.userservice.dto.request.UserRegisterRequest;
import com.elara.userservice.dto.response.UserLoginResponse;
import com.elara.userservice.dto.response.UserLogoutResponse;
import com.elara.userservice.dto.response.UserRegisterResponse;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.enums.UserType;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.model.Company;
import com.elara.userservice.model.User;
import com.elara.userservice.model.UserLogin;
import com.elara.userservice.repository.CompanyRepository;
import com.elara.userservice.repository.UserLoginRepository;
import com.elara.userservice.repository.UserRepository;
import com.elara.userservice.util.JWTTokens;
import com.elara.userservice.util.PasswordEncoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AuthenticationService {

  final UserRepository userRepository;
  final CompanyRepository companyRepository;
  final UserLoginRepository userLoginRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;
  final PasswordEncoder passwordEncoder;
  final JWTTokens jwtTokens;

  public AuthenticationService(UserRepository userRepository,
                               CompanyRepository companyRepository, UserLoginRepository userLoginRepository,
                               ModelMapper modelMapper,
                               MessageService messageService,
                               PasswordEncoder passwordEncoder,
                               JWTTokens jwtTokens) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.userLoginRepository = userLoginRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokens = jwtTokens;
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

    //Send otp verify email
    //Send otp to verify phone

    UserRegisterResponse response = new UserRegisterResponse();
    response.setResponseMessage(messageService.getMessage("User.Register.Success"));
    return response;
  }

  public UserLoginResponse login(UserLoginRequest dto) {
    Company company = companyRepository.findByClientId(RequestUtil.getClientId());
    if (company == null) {
      throw new AppException("Company.Not.Found");
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

    AuthToken authToken = modelMapper.map(user, AuthToken.class);
    Claims claims = jwtTokens.createJWT();
    userLogin.setToken(claims.getSubject());
    authToken.setToken("");
    authToken.setRefreshToken("");
    authToken.setExpires(claims.getExpiration().toString());

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
