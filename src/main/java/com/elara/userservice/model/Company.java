package com.elara.userservice.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "Company")
@Entity
@Getter
@Setter
public class Company {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyName", unique = true)
    private String companyName;

    @Column(name = "companyCode", unique = true)
    private String companyCode;

    @Column(name = "companyAddress")
    private String companyAddress;

    @Column(name = "clientId", unique = true)
    private String clientId;

    @Column(name = "clientSecret")
    private String clientSecret;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
