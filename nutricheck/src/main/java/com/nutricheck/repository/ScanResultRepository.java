package com.nutricheck.repository;

import com.nutricheck.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
}
