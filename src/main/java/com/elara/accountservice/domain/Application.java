package com.elara.accountservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Table(name = "Application")
@Entity
@Getter
@Setter
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "appName", unique = true)
    private String appName;

    @Column(name = "appServer")
    private String appServer;

    @Column(name = "appServerPort")
    private String appServerPort;

    @Column(name = "publicKey")
    private String publicKey;

    @Column(name = "privateKey")
    private String privateKey;

    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;

    @Column(name = "status", nullable = false)
    private String status;
}
