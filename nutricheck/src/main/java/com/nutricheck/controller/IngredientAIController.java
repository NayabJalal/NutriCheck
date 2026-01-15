package com.nutricheck.controller;

import com.nutricheck.service.IngredientAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class IngredientAIController {

    private final IngredientAIService aiService;

    public IngredientAIController(IngredientAIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test(
            @RequestParam(defaultValue = "Sugar, Salt") String message) {

        log.info("Testing AI with ingredients: {}", message);

        return ResponseEntity.ok(
                aiService.analyzeIngredients(message)
        );
    }

    /**
     * Analyze ingredients
     */
    @PostMapping("/analyze-ingredients")
    public ResponseEntity<Map<String, Object>> analyzeIngredients(
            @RequestBody Map<String, String> request) {

        String ingredients = request.get("ingredients");

        if (ingredients == null || ingredients.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ingredients list is required"));
        }

        log.info("Analyzing ingredients");

        return ResponseEntity.ok(
                aiService.analyzeIngredients(ingredients)
        );
    }

    /**
     * Analyze product health
     */
    @PostMapping("/analyze-product")
    public ResponseEntity<Map<String, Object>> analyzeProduct(
            @RequestBody Map<String, String> request) {

        String productName = request.get("productName");
        String ingredients = request.get("ingredients");

        if (productName == null || ingredients == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product name and ingredients are required"));
        }

        log.info("Analyzing product: {}", productName);

        return ResponseEntity.ok(
                aiService.analyzeProductHealth(productName, ingredients)
        );
    }

    /**
     * Get healthier alternatives
     */
    @PostMapping("/alternatives")
    public ResponseEntity<Map<String, Object>> getAlternatives(
            @RequestBody Map<String, String> request) {

        String productName = request.get("productName");

        if (productName == null || productName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product name is required"));
        }

        log.info("Getting alternatives for: {}", productName);

        return ResponseEntity.ok(
                aiService.getAlternatives(productName)
        );
    }

    /**
     * Identify harmful ingredients
     */
    @PostMapping("/identify-harmful")
    public ResponseEntity<Map<String, Object>> identifyHarmful(
            @RequestBody Map<String, String> request) {

        String ingredients = request.get("ingredients");

        if (ingredients == null || ingredients.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ingredients list is required"));
        }

        log.info("Identifying harmful ingredients");

        return ResponseEntity.ok(
                aiService.identifyHarmfulIngredients(ingredients)
        );
    }
}
