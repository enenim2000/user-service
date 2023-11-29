package com.elara.accountservice.service;

import com.elara.accountservice.dto.request.ApplicationRequest;
import com.elara.accountservice.dto.response.ApplicationResponse;
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

  public ApplicationResponse createApplication(ApplicationRequest dto) {
    Application existing = applicationRepository.findByAppName(dto.getAppName());
    if (existing != null) {
      throw new AppException(messageService.getMessage("App.Exist"));
    }

    Application newEntry = modelMapper.map(dto, Application.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());

    newEntry = applicationRepository.save(newEntry);
    ApplicationResponse response = new ApplicationResponse();
    response.setData(modelMapper.map(newEntry, ApplicationResponse.Data.class));
    return response;
  }
}
