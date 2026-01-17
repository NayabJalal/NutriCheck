package com.nutricheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Root response from AI for image/ingredient analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {
    private String productName;
    private List<IngredientAnalysis> results;
    private Integer safetyScore;
    private String overallAssessment;
    private List<String> warningsFor;
}

