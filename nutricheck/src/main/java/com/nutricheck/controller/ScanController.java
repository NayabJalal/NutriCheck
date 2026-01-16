package com.nutricheck.controller;

import com.nutricheck.dto.ScanRequest;
import com.nutricheck.service.AiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//Ai controller
@Slf4j
@RestController
@RequestMapping("/api/scan")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ScanController {

    private final AiService aiService;

    @PostMapping("/ingredients")
    public ResponseEntity<String> generateEmail(@RequestBody ScanRequest scanRequest){
        String response = aiService.generateAiReply(scanRequest);
        return ResponseEntity.ok(response);
    }
}
