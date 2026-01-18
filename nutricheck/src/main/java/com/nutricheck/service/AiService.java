package com.nutricheck.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricheck.dto.AiAnalysisResponse;
import com.nutricheck.dto.ScanRequest;
import com.nutricheck.dto.enums.ProductCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    /**
     * Text-based analysis
     */
    public AiAnalysisResponse generateAiReply(ScanRequest scanRequest) {
        String prompt = buildTextPrompt(scanRequest.getIngredients(), scanRequest.getProductCategory());

        log.info("Calling Gemini for text analysis...");
        String response = chatModel.call(prompt);

        return parseAiResponse(response);
    }

    /**
     * Image-based analysis using the Builder and getText()
     */
    public AiAnalysisResponse analyzeImage(byte[] imageBytes, String mimeType, ProductCategory category) {
        String promptText = buildImagePrompt(category);

        // 1. Create Media object using the updated 1.1.2 package
        var media = new Media(MimeTypeUtils.parseMimeType(mimeType), new ByteArrayResource(imageBytes));

        // 2. FIX: Use UserMessage.builder() for multimodal input
        // This avoids the 'private access' error with the constructor
        var userMessage = UserMessage.builder()
                .text(promptText)
                .media(List.of(media))
                .build();

        log.info("Calling Gemini-2.0-Flash for image analysis...");

        // 3. Execute the call using a Prompt object
        ChatResponse response = chatModel.call(new Prompt(userMessage));

        // 4. FIX: Use getText() to retrieve the content in 1.1.2
        String resultJson = response.getResult().getOutput().getText();

        return parseAiResponse(resultJson);
    }

    private AiAnalysisResponse parseAiResponse(String jsonResponse) {
        try {
            String cleanJson = jsonResponse
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            return objectMapper.readValue(cleanJson, AiAnalysisResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", jsonResponse, e);
            throw new RuntimeException("Invalid AI JSON response", e);
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