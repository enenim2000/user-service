package com.elara.userservice.repository;

import com.elara.userservice.model.User;
import com.elara.userservice.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long>, JpaSpecificationExecutor<UserLogin> {

  UserLogin findByUserId(long userId);
}