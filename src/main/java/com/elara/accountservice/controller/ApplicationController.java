package com.elara.accountservice.controller;

import com.elara.accountservice.auth.Permission;
import com.elara.accountservice.dto.request.ApplicationRequest;
import com.elara.accountservice.dto.response.ApplicationResponse;
import com.elara.accountservice.service.AccountService;
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
@Tag(name = "Application Account Management", description = "Application Account Management")
public class ApplicationController {

  final AccountService accountService;

  public ApplicationController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Operation(summary = "Create New Application or Service")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Create New Application or Service",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ApplicationResponse.class))})})
  @Permission("CREATE_APP")
  @PostMapping("/application/create")
  public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest dto){
    return ResponseEntity.ok(accountService.createApplication(dto));
  }
}
