package com.nutricheck.service;

import com.nutricheck.dto.OllamaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OllamaService {

    private final WebClient webClient;

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    public OllamaService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String ask(String message) {

        try {
            OllamaRequest request = new OllamaRequest();
            request.setModel(model);
            request.setPrompt(message);
            request.setStream(false);

            // Call Ollama API
            String rawResponse = webClient.post()
                    .uri(baseUrl + "/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractResponse(rawResponse);

        } catch (Exception e) {
            e.printStackTrace(); // prints real error in console
            return "Error calling Ollama: " + e.getMessage();
        }
    }

    // Extract only AI text
    private String extractResponse(String raw) {
        if (raw == null) return "No response from Ollama";

        int index = raw.indexOf("\"response\":\"");
        if (index == -1) return raw;

        int start = index + 12;
        int end = raw.indexOf("\"", start);

        if (end == -1) return raw;

        return raw.substring(start, end).replace("\\n", "\n");
    }
}
