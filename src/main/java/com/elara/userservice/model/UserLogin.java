package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;

@Table(name = "UserLogin")
@Entity
@Getter
@Setter
public class UserLogin {

    @Column(name = "userId")
    private long userId; // Id from the User table

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "Status", nullable = false)
    private String status; //Active Deactivated

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
