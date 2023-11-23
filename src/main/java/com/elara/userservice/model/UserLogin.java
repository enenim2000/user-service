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

    @Column(name = "userId")
    private long userId;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "Status", nullable = false)
    private String status; //Active Deactivated

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
