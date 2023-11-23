package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "UserGroupPermission")
@Entity
@Getter
@Setter
public class UserGroupPermission {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "groupId")
    private long groupId;

    @Column(name = " applicationPermissionId")
    private long applicationPermissionId;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
