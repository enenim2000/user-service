package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;

@Table(name = "Application")
@Entity
@Getter
@Setter
public class Application {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "appName")
    private String appName;

    @Column(name = "appServer")
    private String appServer;

    @Column(name = "appServerPort")
    private String appServerPort;

    @Column(name = "publicKey")
    private String publicKey;

    @Column(name = "privateKey")
    private String privateKey;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "Status", nullable = false)
    private String status;
}
