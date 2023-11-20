package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;

@Table(name = "UserGroupPermission")
@Entity
@Getter
@Setter
public class UserGroupPermission {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "groupId")
    private long groupId;

    @Column(name = "permissionId")
    private String permissionId; //From application permission table

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
