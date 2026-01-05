package com.nutricheck.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private HealthProfile healthProfile;
}
