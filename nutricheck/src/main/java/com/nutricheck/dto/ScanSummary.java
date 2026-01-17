package com.nutricheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary statistics for the scan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanSummary {
    private Integer totalIngredients;
    private Integer lowRiskCount;
    private Integer mediumRiskCount;
    private Integer highRiskCount;
    private String overallRisk; // Based on highest risk ingredient
}
