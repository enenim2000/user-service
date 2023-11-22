package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "UserGroup")
@Entity
@Getter
@Setter
public class UserGroup {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "groupId")
    private long groupId;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;
}