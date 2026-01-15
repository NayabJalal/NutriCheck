package com.nutricheck.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricheck.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;
    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String generateAiReply(EmailRequest emailRequest){
        //Build the prompt
        String prompt = buildPrompt(emailRequest);

        //creaft a Request
        /* {
    "contents": [
      {
        "parts": [
          {
            "text": "generate a lie that i can tell my HR demanding for a leave"
          }
        ]
      }
    ]
  } */
        Map<String,Object> requestBody = Map.of(
                "contents" , new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );
         //Do request and get response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //Extract Response and Return response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e){
            return "Error processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        // 1. Set the Persona
        prompt.append("You are a highly qualified Nutritionist and Product Safety Expert. ");
        prompt.append("Analyze the following ingredients list strictly for a ")
                .append(emailRequest.getProductCategory()) // e.g., FOOD, COSMETICS
                .append(" product.\n\n");

        // 2. Add the Ingredients
        prompt.append("### Ingredients List:\n")
                .append(emailRequest.getIngredients())
                .append("\n\n");

        // 3. Define the Response Structure
        prompt.append("### Instructions:\n");
        prompt.append("- Sort the ingredients from LEAST harmful to MOST harmful.\n");
        prompt.append("- Identify any harmful additives, preservatives, or allergens.\n");
        prompt.append("- For each harmful ingredient, explain the specific health risk.\n");
        prompt.append("- Provide a final 'Safety Score' from 1 to 10 (10 being safest).\n");
        prompt.append("- Suggest if there are any specific groups (e.g., children, pregnant women) who should avoid this.\n\n");

//        // 4. Add User's specific question/message if provided
//        if (emailRequest.getMessage() != null && !EmailRequest.getMessage().isBlank()) {
//            prompt.append("### User Specific Question:\n")
//                    .append(emailRequest.getMessage())
//                    .append("\n\n");
//        }

        prompt.append("Keep the analysis factual, concise, and easy for a regular consumer to understand.");
         prompt.append(
                "Format the response in simple sections with short bullet points. " +
                        "Avoid medical jargon. Write as if explaining to a normal consumer. " +
                        "Do NOT use long paragraphs. Keep it easy to read."+
                        "- DO NOT write any introductory or persona sentences."
        );

        return prompt.toString();
    }

}
