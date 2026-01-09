package com.nutricheck.controller;

import com.nutricheck.service.IngredientAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class IngredientAIController {

    private static final Logger log = LoggerFactory.getLogger(IngredientAIController.class);

    private final IngredientAIService aiService;

    public IngredientAIController(IngredientAIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(
            @RequestParam(defaultValue = "Hello") String message) {

        log.info("Testing AI with message: {}", message);

        String response = aiService.analyzeIngredients(message);

        Map<String, String> result = new HashMap<>();
        result.put("request", message);
        result.put("response", response);

        return ResponseEntity.ok(result);
    }

    /**
     * Analyze ingredients
     */
    @PostMapping("/analyze-ingredients")
    public ResponseEntity<Map<String, String>> analyzeIngredients(
            @RequestBody Map<String, String> request) {

        String ingredients = request.get("ingredients");

        if (ingredients == null || ingredients.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ingredients list is required"));
        }

        log.info("Analyzing ingredients: {}", ingredients);

        String analysis = aiService.analyzeIngredients(ingredients);

        Map<String, String> result = new HashMap<>();
        result.put("ingredients", ingredients);
        result.put("analysis", analysis);

        return ResponseEntity.ok(result);
    }

    /**
     * Analyze product health
     */
    @PostMapping("/analyze-product")
    public ResponseEntity<Map<String, String>> analyzeProduct(
            @RequestBody Map<String, String> request) {

        String productName = request.get("productName");
        String ingredients = request.get("ingredients");

        if (productName == null || ingredients == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product name and ingredients are required"));
        }

        log.info("Analyzing product: {}", productName);

        String analysis = aiService.analyzeProductHealth(productName, ingredients);

        Map<String, String> result = new HashMap<>();
        result.put("productName", productName);
        result.put("ingredients", ingredients);
        result.put("analysis", analysis);

        return ResponseEntity.ok(result);
    }

    /**
     * Get healthier alternatives
     */
    @PostMapping("/alternatives")
    public ResponseEntity<Map<String, String>> getAlternatives(
            @RequestBody Map<String, String> request) {

        String productName = request.get("productName");

        if (productName == null || productName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product name is required"));
        }

        log.info("Getting alternatives for: {}", productName);

        String alternatives = aiService.getAlternatives(productName);

        Map<String, String> result = new HashMap<>();
        result.put("productName", productName);
        result.put("alternatives", alternatives);

        return ResponseEntity.ok(result);
    }

    /**
     * Identify harmful ingredients
     */
    @PostMapping("/identify-harmful")
    public ResponseEntity<Map<String, String>> identifyHarmful(
            @RequestBody Map<String, String> request) {

        String ingredients = request.get("ingredients");

        if (ingredients == null || ingredients.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ingredients list is required"));
        }

        log.info("Identifying harmful ingredients");

        String analysis = aiService.identifyHarmfulIngredients(ingredients);

        Map<String, String> result = new HashMap<>();
        result.put("ingredients", ingredients);
        result.put("harmfulAnalysis", analysis);

        return ResponseEntity.ok(result);
    }
}