package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
}
