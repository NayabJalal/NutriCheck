package com.nutricheck.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is Required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
    @Column(unique = true, nullable = false)
    private String email;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private HealthProfile healthProfile;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
