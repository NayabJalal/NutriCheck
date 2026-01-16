package com.nutricheck.dto;

import com.nutricheck.dto.enums.ProductCategory;
import lombok.Data;

@Data
public class ScanRequest {
    private String ingredients;

    private ProductCategory productCategory;


}
