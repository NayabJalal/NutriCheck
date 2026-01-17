package com.nutricheck.service;

import com.nutricheck.dto.ScanResponse;
import com.nutricheck.entity.Scan;
import com.nutricheck.entity.ScanResult;
import com.nutricheck.repository.ScanRepository;
import com.nutricheck.repository.ScanResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final ScanResultRepository scanResultRepository;

    /**
     * Get detailed scan information by ID
     */
    public ScanResponse getScanById(Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new RuntimeException("Scan not found with id: " + scanId));

        return buildScanResponse(scan);
    }

    /**
     * Get all scans for a user
     */
    public List<ScanResponse> getScansByUserId(Long userId) {
        List<Scan> scans = scanRepository.findByUserId(userId);

        return scans.stream()
                .map(this::buildScanResponse)
                .collect(Collectors.toList());
    }

    /**
     * Build complete scan response with all results
     */
    private ScanResponse buildScanResponse(Scan scan) {
        // Get all scan results for this scan using optimized query
        List<ScanResult> scanResults = scanResultRepository.findByScanId(scan.getId());

        // Build result DTOs
        List<com.nutricheck.dto.ScanResultDto> resultDtos = scanResults.stream()
                .map(sr -> com.nutricheck.dto.ScanResultDto.builder()
                        .resultId(sr.getId())
                        .ingredientName(sr.getIngredient().getName())
                        .risk(sr.getRisk())
                        .severity(sr.getSeverity())
                        .explanation(sr.getExplanation())
                        .description(sr.getIngredient().getDescription())
                        .category(sr.getIngredient().getCategory())
                        .sideEffects(sr.getIngredient().getSideEffects())
                        .build())
                .collect(Collectors.toList());

        // Calculate summary statistics
        int lowCount = (int) scanResults.stream()
                .filter(sr -> "LOW".equalsIgnoreCase(sr.getRisk()))
                .count();
        int mediumCount = (int) scanResults.stream()
                .filter(sr -> "MEDIUM".equalsIgnoreCase(sr.getRisk()))
                .count();
        int highCount = (int) scanResults.stream()
                .filter(sr -> "HIGH".equalsIgnoreCase(sr.getRisk()))
                .count();

        String overallRisk = highCount > 0 ? "HIGH" : (mediumCount > 0 ? "MEDIUM" : "LOW");

        com.nutricheck.dto.ScanSummary summary = com.nutricheck.dto.ScanSummary.builder()
                .totalIngredients(scanResults.size())
                .lowRiskCount(lowCount)
                .mediumRiskCount(mediumCount)
                .highRiskCount(highCount)
                .overallRisk(overallRisk)
                .build();

        return ScanResponse.builder()
                .scanId(scan.getId())
                .productName(scan.getProductName())
                .scannedAt(scan.getScannedAt())
                .userId(scan.getUser().getId())
                .userName(scan.getUser().getName())
                .results(resultDtos)
                .summary(summary)
                .build();
    }
}