package com.elara.userservice.controller;

import com.elara.userservice.auth.Permission;
import com.elara.userservice.dto.request.AccessTokenRequest;
import com.elara.userservice.dto.request.NotificationRequest;
import com.elara.userservice.dto.request.TokenVerifyRequest;
import com.elara.userservice.dto.request.UserLoginRequest;
import com.elara.userservice.dto.request.UserRegisterRequest;
import com.elara.userservice.dto.response.AccessTokenResponse;
import com.elara.userservice.dto.response.NotificationResponse;
import com.elara.userservice.dto.response.OtpResendResponse;
import com.elara.userservice.dto.response.OtpVerifyResponse;
import com.elara.userservice.dto.response.TokenVerifyResponse;
import com.elara.userservice.dto.response.UserLoginResponse;
import com.elara.userservice.dto.response.UserLogoutResponse;
import com.elara.userservice.dto.response.UserRegisterResponse;
import com.elara.userservice.service.AuthenticationService;
import com.elara.userservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@Tag(name = "Authentication Management", description = "Authentication Management")
public class AuthenticationController {

  final AuthenticationService authenticationService;
  final NotificationService notificationService;

  public AuthenticationController(AuthenticationService authenticationService,
      NotificationService notificationService) {
    this.authenticationService = authenticationService;
    this.notificationService = notificationService;
  }

  @Operation(summary = "Register New User")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Register New User",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserRegisterResponse.class))})})
  @PostMapping("/register")
  public ResponseEntity<UserRegisterResponse> registerNewUser(@Valid @RequestBody UserRegisterRequest dto) {
    return ResponseEntity.ok(authenticationService.registerUser(dto));
  }

  @Operation(summary = "Login User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = UserLoginResponse.class))})})
  @PostMapping("/login")
  public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody UserLoginRequest dto) {
    return ResponseEntity.ok(authenticationService.login(dto));
  }

  @Operation(summary = "Logout User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Logout User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = UserLogoutResponse.class))})})
  @PutMapping("/logout")
  public ResponseEntity<UserLogoutResponse> logoutUser() {
    return ResponseEntity.ok(authenticationService.logout());
  }

  @Operation(summary = "Obtain New Token Using Refresh Token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Obtain New Token Using Refresh Token",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = TokenVerifyResponse.class))})})
  @GetMapping("/token/refresh")
  public ResponseEntity<AccessTokenResponse> getAccessToken(AccessTokenRequest dto) {
    return ResponseEntity.ok(authenticationService.getAccessTokenFromRefreshToken(dto));
  }

  /**
   *
   * @param request is the request sent by registered service that needs to be secured by the authorization server
   * @return the outcome of the token verification
   */
  @Operation(summary = "Verify User Is Authorized")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Verify User Is Authorized",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = TokenVerifyResponse.class))})})
  @GetMapping("/token/verify")
  public ResponseEntity<TokenVerifyResponse> verifyToken(TokenVerifyRequest request) {
    return ResponseEntity.ok(authenticationService.verifyToken(request));
  }

  @Operation(summary = "Verify Registered Email")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Verify Registered Email",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/email/{otp}/verify")
  @Permission("VERIFY_REGISTERED_EMAIL")
  public ResponseEntity<OtpVerifyResponse> verifyEmailOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.verifyEmailOtp(otp));
  }

  @Operation(summary = "Verify Registered Phone Number")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = " Registered Phone Number",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/phone/{otp}/verify")
  @Permission("VERIFY_REGISTERED_PHONE")
  public ResponseEntity<OtpVerifyResponse> verifyPhoneOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.verifyPhoneOtp(otp));
  }

  @Operation(summary = "Resend Email Verification OTP")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resend Email Verification OTP",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/email/{otp}/resend")
  public ResponseEntity<OtpResendResponse> resendEmailOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.resendEmailOtp(otp));
  }

  @Operation(summary = "Resend Phone Verification OTP")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resend Phone Verification OTP",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/phone/{otp}/resend")
  public ResponseEntity<OtpResendResponse> resendPhoneOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.resendPhoneOtp(otp));
  }

  @Operation(summary = "Send notification")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Send notification",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @PostMapping("/notification")
  public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest dto) {
    notificationService.sendNotification(dto);
    return ResponseEntity.ok(NotificationResponse.builder()
        .build());
  }
}
