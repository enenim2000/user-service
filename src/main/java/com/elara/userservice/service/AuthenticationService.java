package com.elara.userservice.service;

import com.elara.userservice.dto.request.UserRequest;
import com.elara.userservice.dto.response.UserResponse;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.model.User;
import com.elara.userservice.repository.UserRepository;
import com.elara.userservice.util.RequestUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

  final UserRepository userRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;

  public AuthenticationService(UserRepository userRepository, ModelMapper modelMapper,
      MessageService messageService) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
  }

  public UserResponse registerCustomer(UserRequest dto) {
    return null;
  }
}
