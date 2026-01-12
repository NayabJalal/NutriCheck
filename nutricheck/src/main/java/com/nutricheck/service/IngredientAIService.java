package com.nutricheck.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for ingredient analysis using Spring AI with Ollama
 * Stable, local AI setup using llama3
 */
@Service
public class IngredientAIService {

    private static final Logger log =
            LoggerFactory.getLogger(IngredientAIService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ChatModel chatModel;
    public IngredientAIService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /* ------------------------------------------------------
       Public API methods
       ------------------------------------------------------ */

    public Map<String, Object> analyzeIngredients(String ingredientList) {
        return parseAIResponse(
                callAI(buildIngredientAnalysisPrompt(ingredientList))
        );
    }

    public Map<String, Object> analyzeProductHealth(String productName, String ingredientList) {
        return parseAIResponse(
                callAI(buildProductHealthPrompt(productName, ingredientList))
        );
    }

    public Map<String, Object> getAlternatives(String productName) {
        return parseAIResponse(
                callAI(buildAlternativesPrompt(productName))
        );
    }

    public Map<String, Object> identifyHarmfulIngredients(String ingredientList) {
        return parseAIResponse(
                callAI(buildHarmfulIngredientsPrompt(ingredientList))
        );
    }

    /* ------------------------------------------------------
       Core AI call (Spring AI + Ollama)
       ------------------------------------------------------ */

    private String callAI(String promptText) {
        try {
            log.info("Calling AI with prompt length: {}", promptText.length());

            Prompt prompt = new Prompt(
                    promptText,
                    OllamaOptions.builder()
                            .model("llama3")
                            .temperature(0.7)
                            .build()
            );

            ChatResponse response = chatModel.call(prompt);

            return response
                    .getResult()
                    .getOutput()
                    .getContent();

        } catch (Exception e) {
            log.error("❌ Error calling AI", e);
            throw new RuntimeException("AI call failed", e);
        }
    }

    /* ------------------------------------------------------
     JSON parsing (MOST IMPORTANT PART)
     ------------------------------------------------------ */

    private Map<String, Object> parseAIResponse(String aiJson) {
        try {
            return objectMapper.readValue(aiJson, Map.class);
        } catch (Exception e) {
            log.error("❌ Failed to parse AI JSON", e);
            throw new RuntimeException("Invalid AI JSON response", e);
        }
    }

    /* ------------------------------------------------------
       Prompt builders
       ------------------------------------------------------ */

    private String buildIngredientAnalysisPrompt(String ingredientList) {
        return String.format("""
        You are a nutrition expert.

        Analyze the ingredients listed below.

        Ingredients:
        %s

        Respond ONLY in valid JSON.
        Do NOT include explanations, markdown, or extra text.

        Use the following JSON structure exactly:

        {
          "ingredientsAnalysis": [
            {
              "name": "string",
              "riskLevel": "LOW | MEDIUM | HIGH",
              "whatItIs": "string",
              "healthConcerns": "string",
              "whoShouldAvoid": "string"
            }
          ],
          "overallAssessment": "string"
        }

        Rules:
        - Include ALL ingredients from the list
        - riskLevel must be one of: LOW, MEDIUM, HIGH
        - If no specific avoidance is needed, use "General population"
        - Keep explanations concise and factual
        - Base responses on generally accepted nutrition knowledge
        """, ingredientList);
    }

    private String buildProductHealthPrompt(String productName, String ingredientList) {
        return String.format("""
        You are a nutrition expert.

        Analyze the product based on the information below.

        Product: %s
        Ingredients: %s

        Respond ONLY in valid JSON.
        Do NOT include explanations, markdown, or extra text.

        Use the following JSON structure exactly:

        {
          "productName": "string",
          "nutritionalAssessment": "string",
          "healthBenefits": "string",
          "healthConcerns": "string",
          "whoShouldAvoid": "string",
          "overallHealthScore": number
        }

        Rules:
        - overallHealthScore must be a number between 1 and 10
        - Be factual, concise, and evidence-based
        - Avoid exaggerated claims
        - Focus on general nutrition and safety
        """, productName, ingredientList);
    }

    private String buildAlternativesPrompt(String productName) {
        return String.format("""
        You are a nutrition expert.

        Suggest healthier alternatives for the product: %s.

        Respond ONLY in valid JSON.
        Do NOT include explanations, markdown, or extra text.

        Use the following JSON structure exactly:

        {
          "alternatives": [
            {
              "name": "string",
              "whyHealthier": "string",
              "keyNutritionalBenefit": "string"
            }
          ]
        }

        Rules:
        - Provide 3 to 5 alternatives
        - Alternatives must be commonly available products
        - Keep text short and clear
        """, productName);
    }

    private String buildHarmfulIngredientsPrompt(String ingredientList) {
        return String.format("""
        You are a nutrition and food safety expert.

        Identify ONLY potentially harmful or concerning ingredients from the list below.

        Ingredients:
        %s

        Respond ONLY in valid JSON.
        Do NOT include explanations, markdown, or extra text.

        Use the following JSON structure exactly:

        {
          "harmfulIngredients": [
            {
              "name": "string",
              "healthRisks": "string",
              "whoShouldAvoid": "string",
              "safeLimits": "string"
            }
          ]
        }

        Rules:
        - Include only ingredients with proven or suspected health risks
        - If no ingredients are harmful, return an empty array
        - Keep responses concise and factual
        - Base answers on generally accepted nutrition knowledge
        """, ingredientList);
    }
}
