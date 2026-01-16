package com.nutricheck.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricheck.dto.ScanRequest;
import com.nutricheck.dto.enums.ProductCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Existing method for text-based analysis
     */
    public String generateAiReply(ScanRequest scanRequest) {
        String prompt = buildPrompt(scanRequest.getIngredients(), scanRequest.getProductCategory());
        return callGemini(prompt, null, null);
    }

    /**
     * New method to handle image-based analysis
     * Extracts text from image and analyzes it using the same prompt logic
     */
    public String analyzeImage(byte[] imageBytes, String mimeType, ProductCategory category) {
        String prompt = "Extract the product name and ingredients from this image. " +
                "Analyze each for safety risks. " +
                "IMPORTANT: Your response MUST be a single valid JSON object ONLY. " +
                "Do not include markdown, backticks, or any introductory text. " +
                "Use this exact structure: " +
                "{\"productName\": \"string\", \"results\": [{\"ingredientName\": \"string\", \"risk\": \"LOW/MEDIUM/HIGH\", \"severity\": \"string\", \"explanation\": \"string\", \"description\": \"string\"}]}";

        return callGemini(prompt, imageBytes, mimeType);
    }

    private String callGemini(String prompt, byte[] imageBytes, String mimeType) {
        // Construct the parts list
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
            return "Error calling AI: " + e.getMessage();
        }
    }

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
            return "Error processing response: " + e.getMessage();
        }
    }

    // Refactored to be reusable for both text and image flows
    private String buildPrompt(String ingredientList, ProductCategory productCategory) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a highly qualified Nutritionist and Product Safety Expert. ");
        prompt.append("Analyze the product strictly for a ")
                .append(productCategory)
                .append(" product.\n\n");

        if (ingredientList != null && !ingredientList.isBlank()) {
            prompt.append("### Ingredients List:\n")
                    .append(ingredientList)
                    .append("\n\n");
        }

        prompt.append("### Instructions:\n");
        prompt.append("- Sort the ingredients from LEAST harmful to MOST harmful.\n");
        prompt.append("- Identify any harmful additives, preservatives, or allergens.\n");
        prompt.append("- For each harmful ingredient, explain the specific health risk.\n");
        prompt.append("- Provide a final 'Safety Score' from 1 to 10 (10 being safest).\n");
        prompt.append("- Suggest if there are any specific groups (e.g., children, pregnant women) who should avoid this.\n\n");

        prompt.append("Keep the analysis factual, concise, and easy for a regular consumer to understand. ");
        prompt.append("Format the response in simple sections with short bullet points. ")
                .append("Avoid medical jargon. Write as if explaining to a normal consumer. ")
                .append("Do NOT use long paragraphs. Keep it easy to read. ")
                .append("- DO NOT write any introductory or persona sentences.");

        return prompt.toString();
    }
}