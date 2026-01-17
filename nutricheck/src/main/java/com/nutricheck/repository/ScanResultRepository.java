package com.nutricheck.repository;

import com.nutricheck.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {

    /**
     * Find all results for a specific scan
     */
    List<ScanResult> findByScanId(Long scanId);

    /**
     * Find all results by risk level
     */
    List<ScanResult> findByRiskIgnoreCase(String risk);
}