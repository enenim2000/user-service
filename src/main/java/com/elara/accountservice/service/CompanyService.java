package com.elara.accountservice.service;

import com.elara.accountservice.auth.RequestUtil;
import com.elara.accountservice.domain.Company;
import com.elara.accountservice.dto.request.CreateCompanyRequest;
import com.elara.accountservice.dto.request.UpdateCompanyRequest;
import com.elara.accountservice.dto.response.CreateCompanyResponse;
import com.elara.accountservice.dto.response.UpdateCompanyResponse;
import com.elara.accountservice.enums.EntityStatus;
import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.repository.CompanyRepository;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final MessageService messageService;
    private final ModelMapper modelMapper;

    public CompanyService(CompanyRepository companyRepository,
        MessageService messageService, ModelMapper modelMapper) {
        this.companyRepository = companyRepository;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    public CreateCompanyResponse createCompany(CreateCompanyRequest dto) {
        Company existing = companyRepository.findByCompanyName(dto.getCompanyName());
        if (existing != null) {
            throw new AppException(messageService.getMessage("Company.Exist"));
        }

        Company newEntry = modelMapper.map(dto, Company.class);
        newEntry.setCompanyCode(UUID.randomUUID().toString());
        newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
        newEntry.setCreatedAt(new Date());
        newEntry.setStatus(EntityStatus.Enabled.name());

        newEntry = companyRepository.save(newEntry);
        CreateCompanyResponse response = new CreateCompanyResponse();
        response.setData(modelMapper.map(newEntry, CreateCompanyResponse.Data.class));
        return response;
    }

    public UpdateCompanyResponse updateCompany(UpdateCompanyRequest dto) {
        Company existing = companyRepository.findByCompanyName(dto.getCompanyCode());
        if (existing == null) {
            throw new AppException(messageService.getMessage("Company.NotFound"));
        }

        modelMapper.map(dto, existing);
        existing.setUpdatedAt(new Date());
        existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
        existing = companyRepository.save(existing);
        UpdateCompanyResponse response = new UpdateCompanyResponse();
        response.setData(modelMapper.map(existing, UpdateCompanyResponse.Data.class));
        return response;
    }
}
