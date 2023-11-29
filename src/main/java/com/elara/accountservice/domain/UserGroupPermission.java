package com.elara.accountservice.domain;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Table(name = "UserGroupPermission")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupPermission implements Serializable {

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
