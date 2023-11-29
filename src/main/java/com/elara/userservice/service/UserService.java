package com.elara.userservice.service;

import com.elara.userservice.dto.request.UserRequest;
import com.elara.userservice.dto.response.UserResponse;
import com.elara.userservice.enums.EntityStatus;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.domain.User;
import com.elara.userservice.repository.UserRepository;
import com.elara.userservice.auth.RequestUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  final UserRepository userRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;

  public UserService(UserRepository userRepository, ModelMapper modelMapper,
      MessageService messageService) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
  }

  public UserResponse createUser(UserRequest dto) {
    User existing = userRepository.findByEmailOrPhone(dto.getEmail(), dto.getPhone());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Exist"));
    }

    User newEntry = modelMapper.map(dto, User.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());
    newEntry = userRepository.save(newEntry);
    UserResponse response = new UserResponse();
    response.setData(modelMapper.map(newEntry, UserResponse.Data.class));
    return response;
  }
}
