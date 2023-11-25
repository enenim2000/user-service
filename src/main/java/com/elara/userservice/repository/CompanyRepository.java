package com.elara.userservice.repository;

import com.elara.userservice.model.Company;
import com.elara.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  Company findByClientId(String clientId);

  Company findByCompanyCode(String companyCode);
}
