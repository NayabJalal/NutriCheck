package com.nutricheck.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

/**
 * Service for ingredient analysis using Spring AI with Ollama
 * Stable, local AI setup using llama3
 */
@Service
public class IngredientAIService {

    private static final Logger log =
            LoggerFactory.getLogger(IngredientAIService.class);

    private final ChatModel chatModel;

    public IngredientAIService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /* ------------------------------------------------------
       Public API methods
       ------------------------------------------------------ */

    public String analyzeIngredients(String ingredientList) {
        return callAI(buildIngredientAnalysisPrompt(ingredientList));
    }

    public String analyzeProductHealth(String productName, String ingredientList) {
        return callAI(buildProductHealthPrompt(productName, ingredientList));
    }

    public String getAlternatives(String productName) {
        return callAI(buildAlternativesPrompt(productName));
    }

    public String identifyHarmfulIngredients(String ingredientList) {
        return callAI(buildHarmfulIngredientsPrompt(ingredientList));
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

            String result = response
                    .getResult()
                    .getOutput()
                    .getContent();

            log.info("✓ AI response received successfully");
            return result;

        } catch (Exception e) {
            log.error("❌ Error calling AI", e);
            return "Error analyzing with AI: " + e.getMessage();
        }
    }

    /* ------------------------------------------------------
       Prompt builders
       ------------------------------------------------------ */

    private String buildIngredientAnalysisPrompt(String ingredientList) {
        return String.format("""
                You are a nutrition expert. Analyze the following ingredients.

                Ingredients:
                %s

                For each ingredient provide:
                - Risk Level (LOW / MEDIUM / HIGH)
                - What it is
                - Potential health concerns
                - Who should avoid it (if applicable)

                Then give an overall health assessment.
                Keep the answer clear and concise.
                """, ingredientList);
    }

    private String buildProductHealthPrompt(String productName, String ingredientList) {
        return String.format("""
                You are a nutrition expert.

                Product: %s
                Ingredients: %s

                Provide:
                - Nutritional assessment
                - Health benefits
                - Health concerns
                - Who should avoid it
                - Overall health score (1–10)

                Be factual and concise.
                """, productName, ingredientList);
    }

    private String buildAlternativesPrompt(String productName) {
        return String.format("""
                Suggest healthier alternatives for the product: %s

                For each alternative include:
                - Product name
                - Why it is healthier
                - Key nutritional benefit

                Provide practical, commonly available options.
                """, productName);
    }

    private String buildHarmfulIngredientsPrompt(String ingredientList) {
        return String.format("""
                Identify potentially harmful ingredients from the list below.

                Ingredients:
                %s

                For each harmful ingredient include:
                - Name
                - Health risks
                - Who should avoid it
                - Safe limits (if any)

                Only include ingredients with known or suspected risks.
                """, ingredientList);
    }
}
