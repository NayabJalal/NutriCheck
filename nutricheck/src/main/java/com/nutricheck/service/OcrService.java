package com.nutricheck.service;

import com.nutricheck.dto.AiAnalysisResponse;
import com.nutricheck.dto.enums.ProductCategory;
import com.nutricheck.entity.*;
import com.nutricheck.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private final AiService aiService;
    private final ScanRepository scanRepository;
    private final IngredientRepository ingredientRepository;
    private final ScanResultRepository scanResultRepository;
    private final UserRepository userRepository;

    @Transactional
    public Scan processImageScan(byte[] imageBytes, String contentType, Long userId, ProductCategory category) {
        try {
            // 1. Get AI analysis (structured response)
            AiAnalysisResponse aiResponse = aiService.analyzeImage(imageBytes, contentType, category);

            log.info("AI Analysis completed - Product: {}, Ingredients count: {}",
                    aiResponse.getProductName(),
                    aiResponse.getResults() != null ? aiResponse.getResults().size() : 0);

            // 2. Get user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // 3. Create and save Scan
            Scan scan = Scan.builder()
                    .productName(aiResponse.getProductName())
                    .scannedAt(LocalDateTime.now())
                    .user(user)
                    .build();
            scan = scanRepository.save(scan);

            log.info("Created scan ID: {} for product: {}", scan.getId(), aiResponse.getProductName());

            // 4. Process each ingredient analysis result
            if (aiResponse.getResults() != null && !aiResponse.getResults().isEmpty()) {
                for (var analysis : aiResponse.getResults()) {
                    try {
                        // Find or create ingredient in master table
                        Ingredient ingredient = findOrCreateIngredient(
                                analysis.getIngredientName(),
                                analysis.getDescription(),
                                analysis.getCategory(),
                                analysis.getRisk(),
                                analysis.getSideEffects()
                        );

                        // Create scan result linking this scan to the ingredient
                        ScanResult scanResult = ScanResult.builder()
                                .scan(scan)
                                .ingredient(ingredient)
                                .risk(analysis.getRisk())
                                .severity(analysis.getSeverity())
                                .explanation(analysis.getExplanation())
                                .build();

                        scanResultRepository.save(scanResult);

                        log.debug("Saved analysis for ingredient: {} (Risk: {}, Severity: {})",
                                ingredient.getName(), analysis.getRisk(), analysis.getSeverity());

                    } catch (Exception e) {
                        log.error("Error processing ingredient: {}", analysis.getIngredientName(), e);
                        // Continue processing other ingredients even if one fails
                    }
                }
            } else {
                log.warn("No ingredients found in AI response for product: {}", aiResponse.getProductName());
            }

            return scan;

        } catch (Exception e) {
            log.error("Error processing image scan for user: {}", userId, e);
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    /**
     * Find existing ingredient or create new one
     * This prevents duplicate ingredients in the database
     */
    private Ingredient findOrCreateIngredient(
            String name,
            String description,
            String category,
            String riskLevel,
            java.util.List<String> sideEffects
    ) {
        // Use repository method to find by name (case-insensitive)
        return ingredientRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    // Create new ingredient
                    String sideEffectsStr = sideEffects != null && !sideEffects.isEmpty()
                            ? String.join(", ", sideEffects)
                            : null;

                    Ingredient newIngredient = Ingredient.builder()
                            .name(name)
                            .description(description)
                            .category(category)
                            .riskLevel(riskLevel)
                            .sideEffects(sideEffectsStr)
                            .build();

                    Ingredient saved = ingredientRepository.save(newIngredient);
                    log.info("Created new ingredient: {} (Risk: {})", name, riskLevel);
                    return saved;
                });
    }
}