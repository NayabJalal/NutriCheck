package com.nutricheck.controller;

import com.nutricheck.dto.AiRequest;
import com.nutricheck.dto.AiResponse;
import com.nutricheck.service.OllamaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/ask")
    public AiResponse ask(@RequestBody AiRequest request) {
        String reply = ollamaService.ask(request.getMessage());
        return new AiResponse(reply);
    }
}
