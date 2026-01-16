package com.nutricheck.controller;

import com.nutricheck.dto.enums.ProductCategory; // Import the enum
import com.nutricheck.entity.Scan;
import com.nutricheck.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService scanService;

    @PostMapping("/image")
    public ResponseEntity<?> uploadScan(
            @RequestParam("image") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("category") ProductCategory category) { // Added category parameter
        try {
            // Pass the category to the service layer for context-aware AI analysis
            Scan result = scanService.processImageScan(
                    file.getBytes(),
                    file.getContentType(),
                    userId,
                    category
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Processing failed: " + e.getMessage());
        }
    }
}