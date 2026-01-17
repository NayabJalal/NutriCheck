package com.nutricheck.controller;

import com.nutricheck.dto.ScanResponse;
import com.nutricheck.dto.enums.ProductCategory;
import com.nutricheck.entity.Scan;
import com.nutricheck.service.OcrService;
import com.nutricheck.service.ScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OcrController {

    private final OcrService ocrService;
    private final ScanService scanService;

    /**
     * Upload and analyze product image
     * Returns complete analysis with all ingredients
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadScan(
            @RequestParam("image") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "category", defaultValue = "FOOD") String categoryStr) {

        try {
            // Validate category
            ProductCategory category;
            try {
                category = ProductCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid category. Must be: FOOD, COSMETICS, or BEVERAGES"));
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Image file is required"));
            }

            log.info("Processing image scan - User: {}, Category: {}, File size: {} bytes",
                    userId, category, file.getSize());

            // Process the scan
            Scan scan = ocrService.processImageScan(
                    file.getBytes(),
                    file.getContentType(),
                    userId,
                    category
            );

            // Get detailed response
            ScanResponse response = scanService.getScanById(scan.getId());

            log.info("Successfully processed scan ID: {} with {} ingredients",
                    scan.getId(), response.getResults().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process image", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Processing failed: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get scan details by ID
     */
    @GetMapping("/{scanId}")
    public ResponseEntity<ScanResponse> getScan(@PathVariable Long scanId) {
        try {
            ScanResponse response = scanService.getScanById(scanId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving scan: {}", scanId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all scans for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScanResponse>> getUserScans(@PathVariable Long userId) {
        try {
            List<ScanResponse> scans = scanService.getScansByUserId(userId);
            return ResponseEntity.ok(scans);
        } catch (Exception e) {
            log.error("Error retrieving scans for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}