package com.nutricheck.controller;

import com.nutricheck.dto.AiAnalysisResponse;
import com.nutricheck.dto.ScanRequest;
import com.nutricheck.service.AiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/scan")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ScanController {

    private final AiService aiService;
    @PostMapping("/ingredients")
    public ResponseEntity<AiAnalysisResponse> analyzeIngredients(@RequestBody ScanRequest scanRequest) {
        log.info("Analyzing ingredients for category: {}", scanRequest.getProductCategory());
        AiAnalysisResponse response = aiService.generateAiReply(scanRequest);
        return ResponseEntity.ok(response);
    }
}