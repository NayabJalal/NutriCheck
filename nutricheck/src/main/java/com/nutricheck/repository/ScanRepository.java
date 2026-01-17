package com.nutricheck.repository;

import com.nutricheck.entity.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {

    /**
     * Find all scans for a specific user
     */
    List<Scan> findByUserId(Long userId);

    /**
     * Find scans by product name (case-insensitive, partial match)
     */
    List<Scan> findByProductNameContainingIgnoreCase(String productName);
}