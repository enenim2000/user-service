package com.elara.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Table(name = "AccountGroup")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "company")
    private String company;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;
}
