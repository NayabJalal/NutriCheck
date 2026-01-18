package com.nutricheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Individual ingredient analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientAnalysis {
    private String ingredientName;
    private String risk; // LOW, MEDIUM, HIGH
    private String severity; // e.g., "Moderate", "Severe"
    private String explanation;
    private String description;
    private String category;
    private List<String> sideEffects;
    
}
