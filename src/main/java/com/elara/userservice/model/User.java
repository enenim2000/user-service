package com.elara.userservice.model;

import java.util.Date;

import lombok.*;

import javax.persistence.*;

@Table(name = "User")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "middleName")
    private String middleName;

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
