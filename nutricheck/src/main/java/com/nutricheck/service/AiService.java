package com.nutricheck.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricheck.dto.AiAnalysisResponse;
import com.nutricheck.dto.ScanRequest;
import com.nutricheck.dto.enums.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Text-based analysis - returns structured response
     */
    public AiAnalysisResponse generateAiReply(ScanRequest scanRequest) {
        String prompt = buildTextPrompt(scanRequest.getIngredients(), scanRequest.getProductCategory());
        String jsonResponse = callGemini(prompt, null, null);
        return parseAiResponse(jsonResponse);
    }

    /**
     * Image-based analysis - returns structured response
     */
    public AiAnalysisResponse analyzeImage(byte[] imageBytes, String mimeType, ProductCategory category) {
        String prompt = buildImagePrompt(category);
        String jsonResponse = callGemini(prompt, imageBytes, mimeType);
        return parseAiResponse(jsonResponse);
    }

    /**
     * Core method to call Gemini API
     */
    private String callGemini(String prompt, byte[] imageBytes, String mimeType) {
        Object[] parts;

        if (imageBytes != null) {
            // Multimodal request (Text + Image)
            parts = new Object[]{
                    Map.of("text", prompt),
                    Map.of("inline_data", Map.of(
                            "mime_type", mimeType,
                            "data", Base64.getEncoder().encodeToString(imageBytes)
                    ))
            };
        } else {
            // Text-only request
            parts = new Object[]{
                    Map.of("text", prompt)
            };
        }

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", parts)
                }
        );

        try {
            String response = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractResponseContent(response);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("AI call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extract text content from Gemini response
     */
    private String extractResponseContent(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            log.error("Error extracting Gemini response", e);
            throw new RuntimeException("Error processing response: " + e.getMessage(), e);
        }
    }

    /**
     * Parse AI JSON response to structured DTO
     */
    private AiAnalysisResponse parseAiResponse(String jsonResponse) {
        try {
            // Remove markdown code blocks if present
            String cleanJson = jsonResponse
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            log.info("Parsing AI response: {}", cleanJson);

            return objectMapper.readValue(cleanJson, AiAnalysisResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", jsonResponse, e);
            throw new RuntimeException("Invalid AI JSON response: " + e.getMessage(), e);
        }
    }

    /**
     * Build prompt for text-based ingredient analysis
     */
    private String buildTextPrompt(String ingredientList, ProductCategory category) {
        return String.format("""
        You are a Nutritionist and Product Safety Expert analyzing a %s product.

        Ingredients:
        %s

        Analyze each ingredient and respond with ONLY valid JSON. No markdown, no extra text.

        Use this exact structure:
        {
          "productName": "Unknown Product",
          "results": [
            {
              "ingredientName": "string",
              "risk": "LOW | MEDIUM | HIGH",
              "severity": "string (e.g., Minimal, Moderate, Severe)",
              "explanation": "string (brief health concern)",
              "description": "string (what this ingredient is)",
              "category": "string (e.g., preservative, sweetener)",
              "sideEffects": ["string array of potential side effects"]
            }
          ],
          "safetyScore": number (1-10, where 10 is safest),
          "overallAssessment": "string (2-3 sentence summary)",
          "warningsFor": ["string array - groups who should avoid, e.g., pregnant women, children"]
        }

        Rules:
        - Sort ingredients from LEAST to MOST harmful
        - Be factual and concise
        - Include all ingredients from the list
        - Risk levels: LOW, MEDIUM, or HIGH only
        """, category.name(), ingredientList);
    }

    /**
     * Build prompt for image-based analysis
     */
    private String buildImagePrompt(ProductCategory category) {
        return String.format("""
        You are a Nutritionist and Product Safety Expert analyzing a %s product.

        Extract the product name and all ingredients from this image, then analyze each ingredient.

        Respond with ONLY valid JSON. No markdown, no extra text.

        Use this exact structure:
        {
          "productName": "string (extract from image)",
          "results": [
            {
              "ingredientName": "string",
              "risk": "LOW | MEDIUM | HIGH",
              "severity": "string (e.g., Minimal, Moderate, Severe)",
              "explanation": "string (brief health concern)",
              "description": "string (what this ingredient is)",
              "category": "string (e.g., preservative, sweetener)",
              "sideEffects": ["string array of potential side effects"]
            }
          ],
          "safetyScore": number (1-10, where 10 is safest),
          "overallAssessment": "string (2-3 sentence summary)",
          "warningsFor": ["string array - groups who should avoid"]
        }

        Rules:
        - Extract ALL visible ingredients from the image
        - Sort from LEAST to MOST harmful
        - Be factual and concise
        - Risk levels: LOW, MEDIUM, or HIGH only
        """, category.name());
    }
}