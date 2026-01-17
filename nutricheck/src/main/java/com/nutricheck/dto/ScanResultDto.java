package com.nutricheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Individual scan result for an ingredient
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResultDto {
    private Long resultId;
    private String ingredientName;
    private String risk;
    private String severity;
    private String explanation;
    private String description;
    private String category;
    private String sideEffects;
}
