package com.elara.userservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Table(name = "UserLogin")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "userId")
    private long userId;

    @Column(name = "password")
    private String password;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "accessToken")
    private String accessToken;

    @Column(name = "refreshToken")
    private String refreshToken;

}
