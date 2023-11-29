package com.elara.accountservice.repository;

import com.elara.accountservice.domain.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long>, JpaSpecificationExecutor<UserLogin> {

  UserLogin findByUserId(long userId);

  UserLogin findByUserIdAndAccessToken(long userId, String accessToken);

  UserLogin findByUserIdAndRefreshToken(long userId, String refreshToken);
}
