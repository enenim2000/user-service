package com.elara.accountservice.repository;

import com.elara.accountservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @Query("select u from User u where u.companyCode = :companyCode and (u.email = :username or u.phone = :username)")
  User findByCompanyCodeAndEmailOrPhone(@Param("companyCode") String companyCode, @Param("username") String username);

  User findByEmail(String email);

  User findByPhone(String phone);

  @Query("select u from User u where u.email = :username or u.phone = :username")
  User findByUsername(String username);
}
