package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;

@Table(name = "ApplicationPermission")
@Entity
@Getter
@Setter
public class ApplicationPermission {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "permissionId", unique = true)
    private String permissionId; //Hash of appName,http method,uri e.g user-service,GET,/api/user/logout

    @Column(name = "permission")
    private String permission; //The value of PreAuthorize

    @Column(name = "description")
    private String description;

    @Column(name = "uriPath")
    private String uriPath;

    @Column(name = "isSecured")
    private boolean isSecured;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
