package com.elara.accountservice.service;

import com.elara.accountservice.dto.request.CreateApplicationRequest;
import com.elara.accountservice.dto.request.UpdateApplicationRequest;
import com.elara.accountservice.dto.response.CreateApplicationResponse;
import com.elara.accountservice.dto.response.UpdateApplicationResponse;
import com.elara.accountservice.enums.EntityStatus;
import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.domain.Application;
import com.elara.accountservice.repository.ApplicationRepository;
import com.elara.accountservice.auth.RequestUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountService {

  final ApplicationRepository applicationRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;

  public AccountService(ApplicationRepository applicationRepository, ModelMapper modelMapper,
      MessageService messageService) {
    this.applicationRepository = applicationRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
  }

  public CreateApplicationResponse createApplication(CreateApplicationRequest dto) {
    Application existing = applicationRepository.findByAppName(dto.getAppName());
    if (existing != null) {
      throw new AppException(messageService.getMessage("App.Exist"));
    }

    Application newEntry = modelMapper.map(dto, Application.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());

    newEntry = applicationRepository.save(newEntry);
    CreateApplicationResponse response = new CreateApplicationResponse();
    response.setData(modelMapper.map(newEntry, CreateApplicationResponse.Data.class));
    return response;
  }

  public UpdateApplicationResponse updateApplication(UpdateApplicationRequest dto) {
    Application existing = applicationRepository.findByAppName(dto.getAppName());
    if (existing == null) {
      throw new AppException(messageService.getMessage("App.NotFound"));
    }

    modelMapper.map(dto, existing);
    existing.setUpdatedAt(new Date());
    existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
    existing = applicationRepository.save(existing);
    UpdateApplicationResponse response = new UpdateApplicationResponse();
    response.setData(modelMapper.map(existing, UpdateApplicationResponse.Data.class));
    return response;
  }
}
