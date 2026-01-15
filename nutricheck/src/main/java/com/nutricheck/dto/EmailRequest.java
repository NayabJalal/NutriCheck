package com.nutricheck.dto;

import com.nutricheck.dto.enums.ProductCategory;
import lombok.Data;

@Data
public class EmailRequest {
    private String ingredients;

    private ProductCategory productCategory;


}
