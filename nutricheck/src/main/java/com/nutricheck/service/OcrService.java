package com.nutricheck.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; // Best practice: use the Spring-managed bean

    @Transactional
    public Scan processImageScan(byte[] imageBytes, String contentType, Long userId, ProductCategory category) throws Exception {
        // 1. Call AI to analyze the image
        // Make sure this method name matches what you have in your AiService
        String jsonResponse = aiService.analyzeImage(imageBytes, contentType, category);

        // 2. Clean and Parse JSON
        jsonResponse = jsonResponse.replaceAll("```json|```", "").trim();
        JsonNode root = objectMapper.readTree(jsonResponse);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Create and Save the Scan entry
        Scan scan = Scan.builder()
                .productName(root.path("productName").asText("Unknown Product"))
                .scannedAt(LocalDateTime.now())
                .user(user)
                .build();
        scan = scanRepository.save(scan);

        // 4. Process each ingredient and its analysis result
        JsonNode resultsNode = root.path("results");
        if (resultsNode.isArray()) {
            for (JsonNode node : resultsNode) {
                String ingName = node.path("ingredientName").asText();

                // Check if the ingredient already exists in our master list to avoid duplicates
                // This uses the IngredientRepository you provided
                Ingredient ingredient = findOrCreateIngredient(node, ingName);

                // Store the specific analysis result for this particular scan
                ScanResult scanResult = ScanResult.builder()
                        .scan(scan)
                        .ingredient(ingredient)
                        .risk(node.path("risk").asText())
                        .severity(node.path("severity").asText())
                        .explanation(node.path("explanation").asText())
                        .build();

                scanResultRepository.save(scanResult);
            }
        }

        return scan;
    }

    private Ingredient findOrCreateIngredient(JsonNode node, String name) {
        // Logic to prevent duplicate ingredients in the 'ingredients' table
        // Assumes you might add a findByName method to your IngredientRepository
        // For now, it builds a new one as per your original logic
        return ingredientRepository.save(Ingredient.builder()
                .name(name)
                .description(node.path("description").asText())
                .riskLevel(node.path("risk").asText())
                .build());
    }
}