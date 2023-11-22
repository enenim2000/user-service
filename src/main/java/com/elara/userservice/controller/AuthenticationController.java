package com.elara.userservice.controller;

import com.elara.userservice.annotation.Permission;
import com.elara.userservice.dto.request.UserRequest;
import com.elara.userservice.dto.response.UserResponse;
import com.elara.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication Management", description = "Authentication Management")
public class AuthenticationController {

  final UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Register New Customer")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Register New Customer",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserResponse.class))})})
  @PostMapping("/user/create")
  public ResponseEntity<UserResponse> createNewUser(@Valid @RequestBody UserRequest dto){
    return ResponseEntity.ok(userService.createUser(dto));
  }
}
