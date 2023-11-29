package com.elara.accountservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Table(name = "ApplicationAccount")
@Entity
@Getter
@Setter
public class ApplicationAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "userId")
    private long userId;

    @Column(name = "permissionId")
    private String permissionId;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;

    @Column(name = "status", nullable = false)
    private String status;
}