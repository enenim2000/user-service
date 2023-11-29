package com.elara.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Table(name = "ApplicationPermission")
@Entity
@Getter
@Setter
public class ApplicationPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "applicationId")
    private long applicationId;

    @Column(name = "permissionId", unique = true)
    private String permissionId; //Hash SHA 256 of appName,http method,uri e.g user-service,GET,/api/user/logout

    @Column(name = "permission")
    private String permission; //The value of PreAuthorize CREATE_USER

    @Column(name = "description")
    private String description;

    @Column(name = "uriPath")
    private String uriPath;

    @Column(name = "isSecured")
    private boolean isSecured;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;
}
