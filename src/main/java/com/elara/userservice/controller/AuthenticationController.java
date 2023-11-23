package com.elara.userservice.controller;

import com.elara.userservice.dto.request.UserLoginRequest;
import com.elara.userservice.dto.request.UserRegisterRequest;
import com.elara.userservice.dto.response.UserLoginResponse;
import com.elara.userservice.dto.response.UserLogoutResponse;
import com.elara.userservice.dto.response.UserRegisterResponse;
import com.elara.userservice.dto.response.UserResponse;
import com.elara.userservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@Tag(name = "Authentication Management", description = "Authentication Management")
public class AuthenticationController {

  final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
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
}
