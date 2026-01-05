package com.nutricheck.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private LocalDateTime scannedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
