package com.nutricheck.repository;

import com.nutricheck.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Find ingredient by name (case-insensitive)
     * Used to prevent duplicate ingredients
     */
    Optional<Ingredient> findByNameIgnoreCase(String name);
}