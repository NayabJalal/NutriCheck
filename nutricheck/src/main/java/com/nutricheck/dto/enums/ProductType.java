package com.nutricheck.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    // FOOD items
    SNACKS(ProductCategory.FOOD),
    DAIRY(ProductCategory.FOOD),

    // COSMETICS items
    SKINCARE(ProductCategory.COSMETICS),
    HAIRCARE(ProductCategory.COSMETICS),

    // DRINK items
    SODA(ProductCategory.BEVERAGES),
    JUICE(ProductCategory.BEVERAGES);

    private final ProductCategory category;

}
