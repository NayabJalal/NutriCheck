package com.nutricheck.controller;

import com.nutricheck.dto.EmailRequest;
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
public class AiController {

    private final AiService aiService;

    @PostMapping("/ingredients")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response = aiService.generateAiReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
