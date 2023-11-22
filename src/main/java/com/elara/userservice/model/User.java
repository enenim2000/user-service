package com.elara.userservice.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "User")
@Entity
@Getter
@Setter
public class User {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "lang")
    private String lang;

    @Column(name = "isEmailVerified")
    private boolean isEmailVerified;

    @Column(name = "isPhoneVerified")
    private boolean isPhoneVerified;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;
}
