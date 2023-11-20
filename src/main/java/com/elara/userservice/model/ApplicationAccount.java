package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "ApplicationAccount")
@Entity
@Getter
@Setter
public class ApplicationAccount {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "userId")
    private String userId;

    @Column(name = "permissionId")
    private String permissionId;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}