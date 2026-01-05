package com.nutricheck.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String risk;
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne
    @JoinColumn(name = "scan_id")
    private Scan scan;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}
